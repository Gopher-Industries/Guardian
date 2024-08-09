import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:loin_sign_in/get_started.dart';

void main() async{
   WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      locale: Locale('en', 'US'),
      debugShowCheckedModeBanner: false,
      home: GetStarted(),
    );
  }
}

