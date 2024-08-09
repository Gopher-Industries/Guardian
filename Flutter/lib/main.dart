import 'package:flutter/material.dart';
import 'package:flutter_guardian_project/NavigationBars/admin.dart';
import 'package:flutter_guardian_project/NavigationBars/caretaker.dart';
import 'package:flutter_guardian_project/NavigationBars/nurse.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter_app_pages',
      theme: ThemeData(
        useMaterial3: true,
      ),
      debugShowCheckedModeBanner: false,
      home:   Home(),
    );
  }
}

class Home extends StatelessWidget{
  //Modify the below code wth actual home screen (from kotlin or flutter)
  @override
  Widget build(BuildContext context){
    return Scaffold(
      //To test different navigation bars, comment the others and uncomment the one you want to test
      bottomNavigationBar: //Admin(),
      //CareTaker(),
      Nurse(),
    );
  }
}

