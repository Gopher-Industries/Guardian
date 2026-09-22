import streamlit as st
import pandas as pd
import numpy as np
import plotly.express as px
import plotly.graph_objects as go
from plotly.subplots import make_subplots
from sklearn.preprocessing import MinMaxScaler, OneHotEncoder, LabelEncoder
from sklearn.ensemble import IsolationForest, RandomForestClassifier, GradientBoostingClassifier
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix, roc_auc_score
from sklearn.feature_selection import SelectKBest, f_classif
from datetime import datetime, timedelta
import warnings
warnings.filterwarnings('ignore')

# =============================================================================
# CONFIGURATION AND SETUP
# =============================================================================

SHEET_URL = "https://docs.google.com/spreadsheets/d/1qfsk9oR_Ml9Upkbgje8qEKyX8w78SulLiJZ9UIbL0PI/export?format=csv"

st.set_page_config(
    page_title="Advanced Healthcare Dashboard",
    page_icon="🏥",
    layout="wide"
)

# =============================================================================
# ENHANCED DATA LOADING AND PREPROCESSING
# =============================================================================

@st.cache_data
def load_and_preprocess_data():
    """Enhanced data loading with comprehensive feature engineering."""
    try:
        st.info("Loading comprehensive patient data...")
        df = pd.read_csv(SHEET_URL)
        
        # DateTime processing
        if "observationStart" in df.columns:
            df["observationStart"] = pd.to_datetime(df["observationStart"], utc=True, errors="coerce")
        if "observationEnd" in df.columns:
            df["observationEnd"] = pd.to_datetime(df["observationEnd"], utc=True, errors="coerce")
        
        df['patientId'] = df['patientId'].astype(str)
        df['date'] = df['observationStart'].dt.date
        df['hour'] = df['observationStart'].dt.hour
        df['day_of_week'] = df['observationStart'].dt.day_name()
        
        # Clean vital signs
        vitals_cols = ["heartRate", "spo2", "temperature"]
        for col in vitals_cols:
            if col in df.columns:
                df[col] = df[col].astype(str).str.extract(r"(\d+\.?\d*)")[0].astype(float)
        
        # Clean activity and nutrition data
        activity_nutrition_cols = ["stepsTaken", "sleepHours", "exerciseMinutes", 
                                 "calorieIntake", "waterIntakeMl", "bathroomVisits", "mealsSkipped"]
        for col in activity_nutrition_cols:
            if col in df.columns:
                df[col] = pd.to_numeric(df[col], errors='coerce')
        
        # Blood pressure processing
        if 'bloodPressure' in df.columns:
            df['bloodPressure'] = df['bloodPressure'].astype(str)
            df['systolic'] = df['bloodPressure'].str.extract(r'(\d+)').astype(float)
            df['diastolic'] = df['bloodPressure'].str.extract(r'/(\d+)').astype(float)
            df['pulse_pressure'] = df['systolic'] - df['diastolic']
            df['mean_arterial_pressure'] = df['diastolic'] + (df['pulse_pressure'] / 3)
        
        # Clinical risk scores
        if 'heartRate' in df.columns and 'spo2' in df.columns:
            df['cardiopulmonary_risk'] = ((df['heartRate'] - 70) / 30) + ((95 - df['spo2']) / 5)
        
        # Activity ratios and patterns
        if 'stepsTaken' in df.columns and 'exerciseMinutes' in df.columns:
            df['activity_intensity'] = df['stepsTaken'] / (df['exerciseMinutes'] + 1)
        
        if 'calorieIntake' in df.columns and 'waterIntakeMl' in df.columns:
            df['hydration_ratio'] = df['waterIntakeMl'] / (df['calorieIntake'] + 1)
        
        # Sleep quality indicators
        if 'sleepHours' in df.columns:
            df['sleep_quality'] = np.where(
                (df['sleepHours'] >= 7) & (df['sleepHours'] <= 9), 'optimal',
                np.where(df['sleepHours'] < 7, 'insufficient', 'excessive')
            )
        
        # Behavioral processing (simplified flags)
        if 'behaviourTags' in df.columns:
            df['has_behavioral_concerns'] = df['behaviourTags'].notna() & (df['behaviourTags'] != '')
            df['behavioral_severity'] = df['behaviourTags'].str.count(',') + 1
        
        # Fill missing values strategically
        numeric_cols = vitals_cols + activity_nutrition_cols + ['systolic', 'diastolic']
        for col in numeric_cols:
            if col in df.columns:
                df[col] = df[col].fillna(df.groupby('patientId')[col].transform('median'))
                df[col] = df[col].fillna(df[col].median())
        
        # Create daily aggregated data with comprehensive metrics
        daily_df = create_daily_aggregation(df)
        
        st.success(f"Successfully processed {df['patientId'].nunique()} patients, {len(df)} observations")
        return df, daily_df
        
    except Exception as e:
        st.error(f"Error loading data: {str(e)}")
        return None, None

def create_daily_aggregation(df):
    """Create comprehensive daily aggregation with clinical metrics."""
    daily_agg_rules = {
        # Vital signs - average
        'heartRate': 'mean',
        'spo2': 'mean', 
        'temperature': 'mean',
        'systolic': 'mean',
        'diastolic': 'mean',
        'pulse_pressure': 'mean',
        'mean_arterial_pressure': 'mean',
        
        # Activities - sum
        'stepsTaken': 'sum',
        'exerciseMinutes': 'sum',
        'calorieIntake': 'sum',
        'waterIntakeMl': 'sum',
        'bathroomVisits': 'sum',
        'mealsSkipped': 'sum',
        
        # Sleep - average (usually one per day)
        'sleepHours': 'mean',
        
        # Risk scores - max (worst case)
        'cardiopulmonary_risk': 'max',
        
        # Ratios - mean
        'activity_intensity': 'mean',
        'hydration_ratio': 'mean',
        
        # Behavioral - sum/flags
        'has_behavioral_concerns': 'max',
        'behavioral_severity': 'max'
    }
    
    # Filter for available columns
    available_agg = {k: v for k, v in daily_agg_rules.items() if k in df.columns}
    
    daily_df = df.groupby(['patientId', 'date']).agg(available_agg).reset_index()
    
    # Add additional daily metrics
    daily_stats = df.groupby(['patientId', 'date']).agg({
        'heartRate': ['std', 'min', 'max'] if 'heartRate' in df.columns else ['count'],
        'observationStart': 'count'
    }).reset_index()
    
    daily_stats.columns = ['patientId', 'date', 'hr_variability', 'hr_min', 'hr_max', 'daily_observations']
    daily_df = pd.merge(daily_df, daily_stats, on=['patientId', 'date'], how='left')
    
    # Add other important columns
    for col in ['state', 'age', 'gender', 'alerts']:
        if col in df.columns:
            col_daily = df.groupby(['patientId', 'date'])[col].first().reset_index()
            daily_df = pd.merge(daily_df, col_daily, on=['patientId', 'date'], how='left')
    
    return daily_df

@st.cache_data
def train_advanced_health_predictor(daily_df):
    """Advanced health state prediction with comprehensive features."""
    if 'state' not in daily_df.columns or daily_df['state'].isna().all():
        return None, None, None
    
    st.info("Training advanced health prediction model...")
    
    # Comprehensive feature set
    feature_groups = {
        'vital_signs': ['heartRate', 'spo2', 'temperature', 'systolic', 'diastolic'],
        'cardiovascular': ['pulse_pressure', 'mean_arterial_pressure', 'hr_variability'],
        'activity': ['stepsTaken', 'exerciseMinutes', 'activity_intensity'],
        'nutrition': ['calorieIntake', 'waterIntakeMl', 'hydration_ratio', 'mealsSkipped'],
        'sleep': ['sleepHours'],
        'clinical_risk': ['cardiopulmonary_risk'],
        'behavioral': ['has_behavioral_concerns', 'behavioral_severity'],
        'temporal': ['daily_observations'],
        'demographic': ['age']
    }
    
    # Collect available features
    all_features = []
    available_groups = {}
    for group, features in feature_groups.items():
        available = [f for f in features if f in daily_df.columns]
        if available:
            all_features.extend(available)
            available_groups[group] = available
    
    if len(all_features) < 5:
        st.warning("Insufficient features for advanced modeling")
        return None, None, None
    
    # Prepare training data
    model_data = daily_df[all_features + ['patientId', 'state']].copy()
    
    # Advanced feature engineering
    if 'heartRate' in model_data.columns and 'age' in model_data.columns:
        model_data['age_adjusted_hr'] = model_data['heartRate'] / (220 - model_data['age'].fillna(65))
    
    if 'systolic' in model_data.columns and 'diastolic' in model_data.columns:
        model_data['hypertension_risk'] = ((model_data['systolic'] > 140) | 
                                          (model_data['diastolic'] > 90)).astype(int)
    
    # Handle missing values intelligently
    for col in all_features:
        if model_data[col].dtype in ['float64', 'int64']:
            model_data[col] = model_data[col].fillna(model_data.groupby('patientId')[col].transform('median'))
            model_data[col] = model_data[col].fillna(model_data[col].median())
        else:
            model_data[col] = model_data[col].fillna(0)
    
    model_data = model_data.dropna(subset=['state'])
    
    if len(model_data) < 30:
        st.warning("Insufficient data for reliable modeling")
        return None, None, None
    
    # Enhanced features list
    enhanced_features = all_features + ['age_adjusted_hr', 'hypertension_risk']
    enhanced_features = [f for f in enhanced_features if f in model_data.columns]
    
    X = model_data[enhanced_features + ['patientId']]
    y = model_data['state']
    
    # Encode patient IDs
    encoder = OneHotEncoder(handle_unknown='ignore', sparse_output=False)
    patient_encoded = encoder.fit_transform(X[['patientId']])
    
    X_features = X[enhanced_features].fillna(0)
    X_combined = np.hstack([X_features.values, patient_encoded])
    
    # Feature selection
    selector = SelectKBest(score_func=f_classif, k=min(25, X_combined.shape[1]))
    X_selected = selector.fit_transform(X_combined, y)
    
    # Train multiple models and select best
    models = {
        'RandomForest': RandomForestClassifier(n_estimators=100, random_state=42, class_weight='balanced'),
        'GradientBoosting': GradientBoostingClassifier(n_estimators=100, random_state=42)
    }
    
    best_model = None
    best_score = 0
    best_model_name = ""
    
    X_train, X_test, y_train, y_test = train_test_split(X_selected, y, test_size=0.2, random_state=42)
    
    for name, model in models.items():
        model.fit(X_train, y_train)
        score = model.score(X_test, y_test)
        
        if score > best_score:
            best_score = score
            best_model = model
            best_model_name = name
    
    st.success(f"Best model: {best_model_name} with {best_score:.2%} accuracy")
    
    return {
        'model': best_model,
        'encoder': encoder,
        'selector': selector,
        'features': enhanced_features,
        'available_groups': available_groups,
        'model_name': best_model_name
    }, best_score, enhanced_features

@st.cache_data
def generate_clinical_insights(patient_data, daily_data):
    """Generate comprehensive clinical insights."""
    insights = {
        'vital_stability': {},
        'activity_patterns': {},
        'risk_factors': [],
        'recommendations': []
    }
    
    # Vital sign stability analysis
    for vital in ['heartRate', 'spo2', 'temperature']:
        if vital in daily_data.columns and not daily_data[vital].isna().all():
            cv = daily_data[vital].std() / daily_data[vital].mean() * 100
            recent_trend = 'stable'
            
            if len(daily_data) > 7:
                recent_mean = daily_data[vital].tail(7).mean()
                previous_mean = daily_data[vital].iloc[-14:-7].mean() if len(daily_data) > 14 else daily_data[vital].head(7).mean()
                
                if recent_mean > previous_mean * 1.05:
                    recent_trend = 'increasing'
                elif recent_mean < previous_mean * 0.95:
                    recent_trend = 'decreasing'
            
            insights['vital_stability'][vital] = {
                'coefficient_variation': cv,
                'trend': recent_trend,
                'stability': 'stable' if cv < 10 else 'variable'
            }
    
    # Activity pattern analysis
    if 'stepsTaken' in daily_data.columns:
        avg_steps = daily_data['stepsTaken'].mean()
        insights['activity_patterns']['mobility'] = 'active' if avg_steps > 5000 else 'sedentary'
    
    if 'sleepHours' in daily_data.columns:
        avg_sleep = daily_data['sleepHours'].mean()
        insights['activity_patterns']['sleep_pattern'] = 'optimal' if 7 <= avg_sleep <= 9 else 'concerning'
    
    # Risk factor identification
    if 'heartRate' in daily_data.columns:
        if daily_data['heartRate'].mean() > 100:
            insights['risk_factors'].append('Persistent tachycardia')
        elif daily_data['heartRate'].mean() < 60:
            insights['risk_factors'].append('Bradycardia pattern')
    
    if 'spo2' in daily_data.columns:
        if daily_data['spo2'].mean() < 95:
            insights['risk_factors'].append('Chronic hypoxemia')
    
    return insights

def main():
    st.title("🏥 Advanced Healthcare Analytics Dashboard")
    st.markdown("""
    **Comprehensive Clinical Decision Support System**
    
    Advanced features for healthcare professionals:
    • Multi-dimensional health analytics with clinical risk scoring
    • Predictive modeling using comprehensive feature engineering  
    • Real-time vital sign monitoring with stability analysis
    • Clinical insights generation and risk stratification
    • Comprehensive patient activity and nutrition tracking
    """)
    st.markdown("---")
    
    # Load data
    with st.spinner("Loading comprehensive patient database..."):
        df, daily_df = load_and_preprocess_data()
        
        if df is None or daily_df is None:
            st.error("Failed to load patient data")
            return
    
    # Sidebar controls
    st.sidebar.header("🔍 Patient Selection & Filters")
    
    patient_ids = sorted(df['patientId'].unique())
    selected_patient = st.sidebar.selectbox("Select Patient:", patient_ids)
    
    # Date range filter
    if not df.empty and 'observationStart' in df.columns:
        date_range = st.sidebar.date_input(
            "Analysis Period:",
            value=(df['observationStart'].min().date(), df['observationStart'].max().date()),
            min_value=df['observationStart'].min().date(),
            max_value=df['observationStart'].max().date()
        )
    
    # Train advanced model
    with st.spinner("Training advanced clinical prediction models..."):
        model_info, accuracy, features = train_advanced_health_predictor(daily_df)
    
    # Filter data for selected patient
    patient_data = df[df["patientId"] == selected_patient].copy()
    patient_daily = daily_df[daily_df["patientId"] == selected_patient].copy()
    
    # Generate clinical insights
    clinical_insights = generate_clinical_insights(patient_data, patient_daily)
    
    # Patient header with enhanced metrics
    if not patient_data.empty:
        st.subheader(f"👤 Patient Profile: {selected_patient}")
        
        col1, col2, col3, col4, col5 = st.columns(5)
        
        with col1:
            age = patient_data['age'].iloc[0] if 'age' in patient_data.columns else "N/A"
            st.metric("Age", age)
            
        with col2:
            gender = patient_data['gender'].iloc[0] if 'gender' in patient_data.columns else "N/A"
            st.metric("Gender", gender)
            
        with col3:
            days_monitored = len(patient_daily)
            st.metric("Days Monitored", days_monitored)
            
        with col4:
            total_observations = len(patient_data)
            st.metric("Total Observations", total_observations)
            
        with col5:
            current_state = patient_daily['state'].iloc[-1] if 'state' in patient_daily.columns and not patient_daily.empty else "Unknown"
            state_color = "normal" if current_state == "stable" else "inverse"
            st.metric("Current State", current_state, delta_color=state_color)
    
    st.markdown("---")
    
    # Model performance dashboard
    if model_info:
        st.subheader("🤖 Advanced Model Performance")
        col1, col2, col3, col4 = st.columns(4)
        
        with col1:
            st.metric("Model Accuracy", f"{accuracy:.1%}")
        with col2:
            st.metric("Model Type", model_info['model_name'])
        with col3:
            st.metric("Features Used", len(features))
        with col4:
            feature_groups = len(model_info['available_groups'])
            st.metric("Feature Categories", feature_groups)
    
    st.markdown("---")
    
    # Main dashboard tabs
    tab1, tab2, tab3, tab4, tab5, tab6 = st.tabs([
        "📊 Clinical Overview",
        "🫀 Vital Signs Analysis", 
        "🏃 Activity & Wellness",
        "🧠 Behavioral Insights",
        "🚨 Risk Assessment",
        "📈 Predictive Analytics"
    ])
    
    with tab1:
        st.header("📊 Clinical Overview Dashboard")
        
        # Key clinical metrics
        st.subheader("🔍 Key Clinical Indicators")
        
        if not patient_daily.empty:
            col1, col2, col3 = st.columns(3)
            
            with col1:
                st.markdown("**Cardiovascular Status**")
                if 'heartRate' in patient_daily.columns:
                    avg_hr = patient_daily['heartRate'].tail(7).mean()
                    hr_status = "Normal" if 60 <= avg_hr <= 100 else "Abnormal"
                    st.metric("Avg Heart Rate (7d)", f"{avg_hr:.0f} BPM", delta=hr_status)
                
                if 'mean_arterial_pressure' in patient_daily.columns:
                    avg_map = patient_daily['mean_arterial_pressure'].tail(7).mean()
                    st.metric("Mean Arterial Pressure", f"{avg_map:.0f} mmHg")
            
            with col2:
                st.markdown("**Respiratory Status**")
                if 'spo2' in patient_daily.columns:
                    avg_spo2 = patient_daily['spo2'].tail(7).mean()
                    spo2_status = "Normal" if avg_spo2 >= 95 else "Low"
                    st.metric("Avg SpO2 (7d)", f"{avg_spo2:.0f}%", delta=spo2_status)
                
                if 'temperature' in patient_daily.columns:
                    avg_temp = patient_daily['temperature'].tail(7).mean()
                    st.metric("Avg Temperature", f"{avg_temp:.1f}°F")
            
            with col3:
                st.markdown("**Activity Status**")
                if 'stepsTaken' in patient_daily.columns:
                    avg_steps = patient_daily['stepsTaken'].tail(7).mean()
                    activity_status = "Active" if avg_steps > 5000 else "Sedentary"
                    st.metric("Daily Steps (7d)", f"{avg_steps:.0f}", delta=activity_status)
                
                if 'sleepHours' in patient_daily.columns:
                    avg_sleep = patient_daily['sleepHours'].tail(7).mean()
                    st.metric("Avg Sleep", f"{avg_sleep:.1f} hrs")
        
        # Clinical insights summary
        st.subheader("🎯 Clinical Insights Summary")
        
        col1, col2 = st.columns(2)
        
        with col1:
            st.markdown("**Vital Sign Stability**")
            for vital, data in clinical_insights['vital_stability'].items():
                stability_icon = "✅" if data['stability'] == 'stable' else "⚠️"
                trend_icon = {"stable": "➡️", "increasing": "📈", "decreasing": "📉"}.get(data['trend'], "➡️")
                st.write(f"{stability_icon} {vital}: {data['stability'].title()} {trend_icon}")
        
        with col2:
            st.markdown("**Risk Factors**")
            if clinical_insights['risk_factors']:
                for risk in clinical_insights['risk_factors']:
                    st.write(f"🚨 {risk}")
            else:
                st.write("✅ No major risk factors identified")
        
        # Recent alerts and observations
        if 'alerts' in patient_data.columns:
            recent_alerts = patient_data[patient_data['alerts'].notna()].tail(5)
            if not recent_alerts.empty:
                st.subheader("🚨 Recent Clinical Alerts")
                for _, alert_row in recent_alerts.iterrows():
                    alert_date = alert_row['observationStart'].strftime('%Y-%m-%d %H:%M') if pd.notna(alert_row['observationStart']) else 'Unknown'
                    st.warning(f"**{alert_date}**: {alert_row['alerts']}")
    
    with tab2:
        st.header("🫀 Advanced Vital Signs Analysis")
        
        # Multi-vital dashboard
        if 'heartRate' in patient_daily.columns and 'spo2' in patient_daily.columns:
            st.subheader("📈 Comprehensive Vital Signs Monitoring")
            
            # Create multi-plot figure
            fig = make_subplots(
                rows=2, cols=2,
                subplot_titles=('Heart Rate Trend', 'SpO2 Levels', 'Temperature', 'Blood Pressure'),
                specs=[[{"secondary_y": False}, {"secondary_y": False}],
                       [{"secondary_y": False}, {"secondary_y": False}]]
            )
            
            # Heart Rate
            fig.add_trace(
                go.Scatter(x=patient_daily['date'], y=patient_daily['heartRate'], 
                          name='Heart Rate', line=dict(color='red')),
                row=1, col=1
            )
            
            # SpO2
            if 'spo2' in patient_daily.columns:
                fig.add_trace(
                    go.Scatter(x=patient_daily['date'], y=patient_daily['spo2'],
                              name='SpO2', line=dict(color='blue')),
                    row=1, col=2
                )
            
            # Temperature
            if 'temperature' in patient_daily.columns:
                fig.add_trace(
                    go.Scatter(x=patient_daily['date'], y=patient_daily['temperature'],
                              name='Temperature', line=dict(color='green')),
                    row=2, col=1
                )
            
            # Blood Pressure
            if 'systolic' in patient_daily.columns and 'diastolic' in patient_daily.columns:
                fig.add_trace(
                    go.Scatter(x=patient_daily['date'], y=patient_daily['systolic'],
                              name='Systolic', line=dict(color='purple')),
                    row=2, col=2
                )
                fig.add_trace(
                    go.Scatter(x=patient_daily['date'], y=patient_daily['diastolic'],
                              name='Diastolic', line=dict(color='orange')),
                    row=2, col=2
                )
            
            fig.update_layout(height=600, template="plotly_white", showlegend=True)
            st.plotly_chart(fig, use_container_width=True)
            
            # Vital signs statistics
            st.subheader("📊 Vital Signs Statistics")
            
            vital_stats = {}
            for vital in ['heartRate', 'spo2', 'temperature', 'systolic', 'diastolic']:
                if vital in patient_daily.columns and not patient_daily[vital].isna().all():
                    vital_stats[vital] = {
                        'mean': patient_daily[vital].mean(),
                        'std': patient_daily[vital].std(),
                        'min': patient_daily[vital].min(),
                        'max': patient_daily[vital].max()
                    }
            
            if vital_stats:
                stats_df = pd.DataFrame(vital_stats).T
                stats_df = stats_df.round(2)
                st.dataframe(stats_df, use_container_width=True)
    
    with tab3:
        st.header("🏃 Activity & Wellness Monitoring")
        
        # Activity overview
        st.subheader("📊 Daily Activity Summary")
        
        if not patient_daily.empty:
            activity_cols = ['stepsTaken', 'exerciseMinutes', 'sleepHours', 'calorieIntake', 'waterIntakeMl']
            available_activity = [col for col in activity_cols if col in patient_daily.columns]
            
            if available_activity:
                # Activity trends
                for col in available_activity:
                    if not patient_daily[col].isna().all():
                        fig = px.line(patient_daily, x='date', y=col, 
                                     title=f"{col.replace('_', ' ').title()} Over Time",
                                     markers=True)
                        fig.update_layout(template="plotly_white")
                        
                        with st.expander(f"📈 {col.replace('_', ' ').title()}", expanded=False):
                            st.plotly_chart(fig, use_container_width=True)
                            
                            # Activity insights
                            recent_avg = patient_daily[col].tail(7).mean()
                            overall_avg = patient_daily[col].mean()
                            
                            col1, col2, col3 = st.columns(3)
                            with col1:
                                st.metric("Recent Average (7d)", f"{recent_avg:.1f}")
                            with col2:
                                st.metric("Overall Average", f"{overall_avg:.1f}")
                            with col3:
                                change = ((recent_avg - overall_avg) / overall_avg * 100) if overall_avg != 0 else 0
                                st.metric("Change", f"{change:+.1f}%")
                
                # Wellness score calculation
                st.subheader("🌟 Wellness Score")
                
                wellness_factors = {}
                total_score = 0
                max_score = 0
                
                if 'stepsTaken' in patient_daily.columns:
                    avg_steps = patient_daily['stepsTaken'].tail(7).mean()
                    step_score = min(100, (avg_steps / 10000) * 100)
                    wellness_factors['Activity'] = step_score
                    total_score += step_score
                    max_score += 100
                
                if 'sleepHours' in patient_daily.columns:
                    avg_sleep = patient_daily['sleepHours'].tail(7).mean()
                    sleep_score = 100 if 7 <= avg_sleep <= 9 else max(0, 100 - abs(avg_sleep - 8) * 20)
                    wellness_factors['Sleep'] = sleep_score
                    total_score += sleep_score
                    max_score += 100
                
                if 'exerciseMinutes' in patient_daily.columns:
                    avg_exercise = patient_daily['exerciseMinutes'].tail(7).mean()
                    exercise_score = min(100, (avg_exercise / 30) * 100)
                    wellness_factors['Exercise'] = exercise_score
                    total_score += exercise_score
                    max_score += 100
                
                if max_score > 0:
                    overall_wellness = (total_score / max_score) * 100
                    
                    col1, col2 = st.columns(2)
                    with col1:
                        st.metric("Overall Wellness Score", f"{overall_wellness:.0f}/100")
                        
                        if overall_wellness >= 80:
                            st.success("Excellent wellness profile")
                        elif overall_wellness >= 60:
                            st.info("Good wellness with room for improvement")
                        else:
                            st.warning("Wellness needs attention")
                    
                    with col2:
                        fig_wellness = px.bar(
                            x=list(wellness_factors.keys()),
                            y=list(wellness_factors.values()),
                            title="Wellness Component Scores",
                            color=list(wellness_factors.values()),
                            color_continuous_scale="RdYlGn"
                        )
                        fig_wellness.update_layout(template="plotly_white")
                        st.plotly_chart(fig_wellness, use_container_width=True)
    
    with tab4:
        st.header("🧠 Behavioral & Psychological Insights")
        
        st.info("**Behavioral Analysis**: Understanding psychological patterns that impact health outcomes")
        
        if 'behaviourTags' in patient_data.columns or 'emotionTags' in patient_data.columns:
            # Behavioral timeline
            behavioral_data = patient_data[patient_data['behaviourTags'].notna() | patient_data['emotionTags'].notna()].copy()
            
            if not behavioral_data.empty:
                st.subheader("📅 Behavioral Timeline")
                
                col1, col2, col3 = st.columns(3)
                with col1:
                    total_behavioral_events = len(behavioral_data)
                    st.metric("Total Behavioral Events", total_behavioral_events)
                
                with col2:
                    if 'has_behavioral_concerns' in patient_daily.columns:
                        days_with_concerns = patient_daily['has_behavioral_concerns'].sum()
                        st.metric("Days with Concerns", int(days_with_concerns))
                
                with col3:
                    if 'behavioral_severity' in patient_daily.columns:
                        avg_severity = patient_daily['behavioral_severity'].mean()
                        st.metric("Avg Severity Score", f"{avg_severity:.1f}")
                
                # Recent behavioral observations
                st.subheader("📋 Recent Behavioral Observations")
                
                recent_behavioral = behavioral_data.tail(10)
                for _, row in recent_behavioral.iterrows():
                    date_str = row['observationStart'].strftime('%Y-%m-%d %H:%M') if pd.notna(row['observationStart']) else 'Unknown'
                    
                    with st.expander(f"🗓️ {date_str}"):
                        if 'behaviourTags' in row and pd.notna(row['behaviourTags']):
                            st.write(f"**Behavior**: {row['behaviourTags']}")
                        if 'emotionTags' in row and pd.notna(row['emotionTags']):
                            st.write(f"**Emotion**: {row['emotionTags']}")
                        
                        # Show concurrent vitals
                        vital_context = []
                        if 'heartRate' in row and pd.notna(row['heartRate']):
                            vital_context.append(f"HR: {row['heartRate']:.0f}")
                        if 'spo2' in row and pd.notna(row['spo2']):
                            vital_context.append(f"SpO2: {row['spo2']:.0f}%")
                        
                        if vital_context:
                            st.write(f"**Concurrent Vitals**: {', '.join(vital_context)}")
                
                # Behavioral correlation analysis
                if 'heartRate' in patient_data.columns and 'has_behavioral_concerns' in patient_data.columns:
                    st.subheader("🔗 Behavioral-Physiological Correlations")
                    
                    behavioral_vitals = patient_data[['has_behavioral_concerns', 'heartRate', 'spo2', 'temperature']].dropna()
                    
                    if not behavioral_vitals.empty and behavioral_vitals['has_behavioral_concerns'].sum() > 0:
                        concern_hr = behavioral_vitals[behavioral_vitals['has_behavioral_concerns'] == True]['heartRate'].mean()
                        normal_hr = behavioral_vitals[behavioral_vitals['has_behavioral_concerns'] == False]['heartRate'].mean()
                        
                        col1, col2, col3 = st.columns(3)
                        
                        with col1:
                            st.metric("HR During Concerns", f"{concern_hr:.0f} BPM")
                        with col2:
                            st.metric("HR Normal Times", f"{normal_hr:.0f} BPM")
                        with col3:
                            hr_difference = concern_hr - normal_hr
                            st.metric("Difference", f"{hr_difference:+.0f} BPM")
                        
                        if abs(hr_difference) > 10:
                            st.warning("Significant heart rate changes during behavioral events")
            else:
                st.info("No behavioral data recorded for this patient")
        else:
            st.info("Behavioral tracking not available - consider implementing for comprehensive care")
    
    with tab5:
        st.header("🚨 Clinical Risk Assessment")
        
        st.subheader("⚠️ Risk Stratification Dashboard")
        
        # Calculate comprehensive risk scores
        risk_scores = {}
        risk_alerts = []
        
        # Cardiovascular risk
        if 'heartRate' in patient_daily.columns and 'age' in patient_daily.columns:
            avg_hr = patient_daily['heartRate'].tail(7).mean()
            age = patient_data['age'].iloc[0] if not patient_data.empty else 65
            
            # Age-adjusted heart rate risk
            max_hr = 220 - age
            hr_reserve = max_hr - avg_hr
            
            cv_risk = 0
            if avg_hr > 100:
                cv_risk += 30
                risk_alerts.append("Persistent tachycardia detected")
            elif avg_hr < 50:
                cv_risk += 25
                risk_alerts.append("Bradycardia pattern identified")
            
            if 'hr_variability' in patient_daily.columns:
                hr_var = patient_daily['hr_variability'].tail(7).mean()
                if hr_var > 20:
                    cv_risk += 15
                    risk_alerts.append("High heart rate variability")
            
            risk_scores['Cardiovascular'] = min(100, cv_risk)
        
        # Respiratory risk
        if 'spo2' in patient_daily.columns:
            avg_spo2 = patient_daily['spo2'].tail(7).mean()
            
            resp_risk = 0
            if avg_spo2 < 90:
                resp_risk += 50
                risk_alerts.append("Severe hypoxemia")
            elif avg_spo2 < 95:
                resp_risk += 25
                risk_alerts.append("Chronic hypoxemia")
            
            risk_scores['Respiratory'] = resp_risk
        
        # Activity risk
        if 'stepsTaken' in patient_daily.columns:
            avg_steps = patient_daily['stepsTaken'].tail(7).mean()
            
            activity_risk = 0
            if avg_steps < 1000:
                activity_risk += 40
                risk_alerts.append("Severe physical inactivity")
            elif avg_steps < 3000:
                activity_risk += 20
                risk_alerts.append("Low physical activity")
            
            risk_scores['Physical Activity'] = activity_risk
        
        # Sleep risk
        if 'sleepHours' in patient_daily.columns:
            avg_sleep = patient_daily['sleepHours'].tail(7).mean()
            
            sleep_risk = 0
            if avg_sleep < 5:
                sleep_risk += 30
                risk_alerts.append("Severe sleep deprivation")
            elif avg_sleep < 6 or avg_sleep > 10:
                sleep_risk += 15
                risk_alerts.append("Sleep pattern irregularity")
            
            risk_scores['Sleep'] = sleep_risk
        
        # Behavioral risk
        if 'has_behavioral_concerns' in patient_daily.columns:
            behavioral_days = patient_daily['has_behavioral_concerns'].sum()
            total_days = len(patient_daily)
            
            behavioral_risk = 0
            if behavioral_days > total_days * 0.3:
                behavioral_risk += 25
                risk_alerts.append("Frequent behavioral concerns")
            elif behavioral_days > 0:
                behavioral_risk += 10
            
            risk_scores['Behavioral'] = behavioral_risk
        
        # Display risk dashboard
        if risk_scores:
            col1, col2 = st.columns(2)
            
            with col1:
                st.markdown("**Risk Category Scores**")
                
                # Overall risk calculation
                overall_risk = np.mean(list(risk_scores.values())) if risk_scores else 0
                
                if overall_risk < 15:
                    risk_level = "Low"
                    risk_color = "normal"
                elif overall_risk < 35:
                    risk_level = "Moderate" 
                    risk_color = "inverse"
                else:
                    risk_level = "High"
                    risk_color = "inverse"
                
                st.metric("Overall Risk Score", f"{overall_risk:.0f}/100", delta=risk_level, delta_color=risk_color)
                
                # Individual risk scores
                for category, score in risk_scores.items():
                    risk_status = "High" if score > 30 else "Moderate" if score > 15 else "Low"
                    st.metric(f"{category} Risk", f"{score:.0f}/100", delta=risk_status)
            
            with col2:
                st.markdown("**Risk Visualization**")
                
                fig_risk = px.bar(
                    x=list(risk_scores.keys()),
                    y=list(risk_scores.values()),
                    title="Risk Category Breakdown",
                    color=list(risk_scores.values()),
                    color_continuous_scale="Reds"
                )
                fig_risk.update_layout(template="plotly_white", showlegend=False)
                st.plotly_chart(fig_risk, use_container_width=True)
        
        # Risk alerts
        st.subheader("🚨 Active Risk Alerts")
        
        if risk_alerts:
            for alert in risk_alerts:
                st.error(f"⚠️ {alert}")
        else:
            st.success("✅ No major risk factors identified")
        
        # Risk mitigation recommendations
        st.subheader("💡 Risk Mitigation Recommendations")
        
        recommendations = []
        
        if 'Cardiovascular' in risk_scores and risk_scores['Cardiovascular'] > 20:
            recommendations.append("Consider cardiology consultation and continuous cardiac monitoring")
        
        if 'Respiratory' in risk_scores and risk_scores['Respiratory'] > 20:
            recommendations.append("Respiratory assessment and potential oxygen therapy evaluation")
        
        if 'Physical Activity' in risk_scores and risk_scores['Physical Activity'] > 20:
            recommendations.append("Physical therapy referral and structured mobility program")
        
        if 'Sleep' in risk_scores and risk_scores['Sleep'] > 20:
            recommendations.append("Sleep study evaluation and sleep hygiene intervention")
        
        if 'Behavioral' in risk_scores and risk_scores['Behavioral'] > 20:
            recommendations.append("Behavioral health assessment and intervention planning")
        
        if not recommendations:
            recommendations = ["Continue current monitoring protocol", "Maintain preventive care measures"]
        
        for i, rec in enumerate(recommendations, 1):
            st.write(f"**{i}.** {rec}")
    
    with tab6:
        st.header("📈 Predictive Analytics & Forecasting")
        
        if model_info and not patient_daily.empty:
            st.subheader("🔮 Health State Prediction Analysis")
            
            # Model insights
            col1, col2, col3 = st.columns(3)
            
            with col1:
                st.metric("Prediction Model", model_info['model_name'])
            with col2:
                st.metric("Model Accuracy", f"{accuracy:.1%}")
            with col3:
                feature_categories = len(model_info['available_groups'])
                st.metric("Data Dimensions", feature_categories)
            
            # Feature importance analysis
            if hasattr(model_info['model'], 'feature_importances_'):
                st.subheader("🔍 Key Prediction Factors")
                
                # Get feature importance
                importance_scores = model_info['model'].feature_importances_
                
                if len(importance_scores) > 0:
                    # Create importance visualization
                    n_top_features = min(10, len(importance_scores))
                    top_indices = np.argsort(importance_scores)[-n_top_features:]
                    
                    importance_data = {
                        'Feature_Index': top_indices,
                        'Importance': importance_scores[top_indices]
                    }
                    
                    fig_importance = px.bar(
                        x=importance_data['Importance'],
                        y=[f"Feature_{i}" for i in importance_data['Feature_Index']],
                        orientation='h',
                        title="Top Predictive Features",
                        labels={'x': 'Importance Score', 'y': 'Feature'}
                    )
                    fig_importance.update_layout(template="plotly_white")
                    st.plotly_chart(fig_importance, use_container_width=True)
            
            # Trend analysis and prediction
            st.subheader("📊 Health Trend Analysis")
            
            # Analyze trends in key metrics
            trend_metrics = ['heartRate', 'spo2', 'stepsTaken', 'sleepHours']
            available_trends = [col for col in trend_metrics if col in patient_daily.columns]
            
            if available_trends and len(patient_daily) > 7:
                for metric in available_trends[:2]:  # Show top 2 trends
                    if not patient_daily[metric].isna().all():
                        # Calculate trend
                        recent_data = patient_daily[metric].tail(14).dropna()
                        
                        if len(recent_data) > 7:
                            # Simple linear trend
                            x = np.arange(len(recent_data))
                            coeffs = np.polyfit(x, recent_data, 1)
                            trend_slope = coeffs[0]
                            
                            # Trend interpretation
                            if abs(trend_slope) < recent_data.std() * 0.1:
                                trend_desc = "Stable"
                                trend_color = "normal"
                            elif trend_slope > 0:
                                trend_desc = "Increasing"
                                trend_color = "normal" if metric in ['stepsTaken', 'sleepHours', 'spo2'] else "inverse"
                            else:
                                trend_desc = "Decreasing" 
                                trend_color = "inverse" if metric in ['stepsTaken', 'sleepHours', 'spo2'] else "normal"
                            
                            # Create trend visualization
                            fig_trend = px.line(
                                x=patient_daily['date'].tail(14),
                                y=patient_daily[metric].tail(14),
                                title=f"{metric} 14-Day Trend",
                                markers=True
                            )
                            
                            # Add trend line
                            trend_line = coeffs[0] * x + coeffs[1]
                            fig_trend.add_scatter(
                                x=patient_daily['date'].tail(14),
                                y=trend_line,
                                mode='lines',
                                name='Trend',
                                line=dict(dash='dash', color='red')
                            )
                            
                            fig_trend.update_layout(template="plotly_white")
                            
                            with st.expander(f"📈 {metric} Trend Analysis", expanded=False):
                                col1, col2 = st.columns(2)
                                
                                with col1:
                                    st.metric("Trend Direction", trend_desc, delta_color=trend_color)
                                    st.metric("Slope", f"{trend_slope:.3f}")
                                
                                with col2:
                                    st.plotly_chart(fig_trend, use_container_width=True)
            
            # Predictive alerts
            st.subheader("🚨 Predictive Risk Alerts")
            
            predictive_alerts = []
            
            # Check for deteriorating trends
            if 'heartRate' in patient_daily.columns and len(patient_daily) > 10:
                recent_hr = patient_daily['heartRate'].tail(7).mean()
                previous_hr = patient_daily['heartRate'].iloc[-14:-7].mean() if len(patient_daily) > 14 else patient_daily['heartRate'].head(7).mean()
                
                if recent_hr > previous_hr * 1.15:
                    predictive_alerts.append("Heart rate showing concerning upward trend")
                elif recent_hr < previous_hr * 0.85:
                    predictive_alerts.append("Heart rate showing significant decline")
            
            if 'spo2' in patient_daily.columns and len(patient_daily) > 10:
                recent_spo2 = patient_daily['spo2'].tail(7).mean()
                previous_spo2 = patient_daily['spo2'].iloc[-14:-7].mean() if len(patient_daily) > 14 else patient_daily['spo2'].head(7).mean()
                
                if recent_spo2 < previous_spo2 * 0.95:
                    predictive_alerts.append("Oxygen saturation declining - respiratory monitoring advised")
            
            if 'stepsTaken' in patient_daily.columns and len(patient_daily) > 10:
                recent_activity = patient_daily['stepsTaken'].tail(7).mean()
                previous_activity = patient_daily['stepsTaken'].iloc[-14:-7].mean() if len(patient_daily) > 14 else patient_daily['stepsTaken'].head(7).mean()
                
                if recent_activity < previous_activity * 0.7:
                    predictive_alerts.append("Significant decline in physical activity detected")
            
            if predictive_alerts:
                for alert in predictive_alerts:
                    st.warning(f"🔮 {alert}")
            else:
                st.success("✅ No predictive risk factors identified")
        else:
            st.info("Predictive analytics require more data or model training")
    
    # Footer with system information
    st.markdown("---")
    
    col1, col2, col3, col4 = st.columns(4)
    
    with col1:
        st.markdown("**📊 Data Quality**")
        if not patient_data.empty:
            completeness = (1 - patient_data.select_dtypes(include=[np.number]).isna().mean().mean()) * 100
            st.metric("Data Completeness", f"{completeness:.1f}%")
    
    with col2:
        st.markdown("**🤖 AI Status**")
        ai_status = "Active" if model_info else "Limited"
        st.metric("Prediction Model", ai_status)
    
    with col3:
        st.markdown("**📈 Monitoring**")
        if not patient_daily.empty:
            st.metric("Days Tracked", len(patient_daily))
    
    with col4:
        st.markdown("**⚡ Last Update**")
        if not patient_data.empty and 'observationStart' in patient_data.columns:
            last_update = patient_data['observationStart'].max()
            hours_ago = (datetime.now(last_update.tz) - last_update).total_seconds() / 3600
            st.metric("Hours Ago", f"{hours_ago:.1f}")
    
    st.markdown("""
    ---
    **🏥 Advanced Healthcare Analytics Dashboard**  
    *Clinical Decision Support • Predictive Analytics • Risk Assessment • Comprehensive Monitoring*
    
    **Key Features:**
    • **Multi-dimensional Analytics**: Comprehensive health monitoring across all vital systems
    • **Predictive Risk Assessment**: AI-powered early warning system for health deterioration  
    • **Clinical Decision Support**: Evidence-based recommendations and risk stratification
    • **Behavioral Integration**: Understanding psychological factors in health outcomes
    • **Advanced Visualization**: Interactive charts and trend analysis for clinical insights
    
    *This system provides clinical decision support tools. All recommendations should be validated by qualified healthcare professionals.*
    """)

if __name__ == "__main__":
    main()