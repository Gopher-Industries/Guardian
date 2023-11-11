## What is Guardian?

**Overview, Goals and Objectives**
Guardians is an activity monitoring and profiling system for the aged care sector with plans to
expand to residential homes. Guardians uses non-invasive radar technology to record a user’s
activity and classify these activities over time. Guardians utilises the classified data to predict
possible physical and mental health conditions associated with that behaviour.

The goal of the project is to alert the patient’s carer and/or health professional to unusual
changes inmonitored behaviour. Additionally, Guardians detects more urgent incidents such as fall
detection.The key features of Guardian are:

- Patent profile capture, edit and search.
- Monitoring and profiling patient activities.
- Alerting caregivers and supervisors during an emergency.
- Predicting potential physical and mental health conditions at an early stage.
- Providing suggestions to see a health professional based on health issues detected.
- Encouraging positive activities.
- Generating weekly and monthly activity dashboards.
- Providing access to accurate real-time visualisation and patient position(s).
- Ability to define a baseline for a patient.
- User management allows for defining Patient, Carer, and Admin users.

Overview of Gopher in T2 2023:
[Click here](https://deakin365.sharepoint.com/:p:/r/sites/GopherIndustries2/_layouts/15/Doc.aspx?sourcedoc=%7B0C4E7C6C-2873-423D-BBF0-1CE09D4B526D%7D&file=Junior%20Gopher%20Industries%20Presentation.pptx&action=edit&mobileredirect=true)(
This link doesn't work, we can replace with T2 handover video)

## User Manual

https://deakin365.sharepoint.com/:w:/s/GopherIndustries2/EYjVSnG50B1GvYgdT2KKMj0Bj4-vwmNHitYyU0fzGUk_RA?e=IrpJZt

## Branching strategy:

Because this repository is protected, you have to fork to your own repository before cloning. Please
follow [steps](https://deakin365.sharepoint.com/:w:/r/sites/GopherIndustries2/Shared%20Documents/Guardians%20(T1)/T1%202023/TeamGuardians-CloneProcess.docx?d=wfba6c34a53b743c4a39b519990def465&csf=1&web=1&e=PfBjyp)
to clone this
repo. [Click here](https://deakin365.sharepoint.com/:w:/s/GopherIndustries2/EaQj9mdwqT1Jk505WQZqK0gBSJ3pDyz-Rz6naKJPP15m5w?e=n0MqIn)
to find how to create a pull request after you've done wonderful development.

## How to set up Android Studio :

in [Click here](https://deakin365.sharepoint.com/:w:/s/GopherIndustries2/EdSrJiI562ZCj2gx4QXkCRYBU58s5W6MNCDr3yDlcHXcog?e=oCLfc1),
you can learn how to install Android Studio, initial set and create an emulator.

## Linting

This project uses the Spotless formatter for Java and Kotlin code in GitHub Actions.
This means that all code pushed to the repository will be automatically formatted.
If you want to format your code locally, you can run the following command:

```bash
chmod +x gradlew
./gradlew build
# If there are formatting errors that are auto-fixable, run
./gradlew spotlessApply
```

If you want to install the Spotless plugin for IntelliJ IDEA, you can find it here:

https://plugins.jetbrains.com/plugin/18321-spotless-gradle

This will allow you to format your code in the IDE, using the same rules as the GitHub Actions
workflow. You can also configure the plugin to format your code on save.
Go to Settings > Tools > Actions on Save and tick all checkboxes.
