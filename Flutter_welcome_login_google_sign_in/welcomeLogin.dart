import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:loin_sign_in/functions/FunctionsForLogin.dart';
import 'package:loin_sign_in/functions/authFunctions.dart';
import 'package:loin_sign_in/googlesigninbutton.dart';
import 'package:loin_sign_in/welcomeLoginTablet.dart';

class Welcome extends StatefulWidget {
  @override
  _WelcomeState createState() => _WelcomeState();
}

class _WelcomeState extends State<Welcome> {
  
  Functionsforlogin functionsForLogin = Functionsforlogin();
  int _selectedRole = 1; // Default selection (e.g., Nurse)

  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (BuildContext context, BoxConstraints constraints) {
        double width = constraints.maxWidth;
        double height = constraints.maxHeight;
        bool isTablet = width >= 600;
        double h = MediaQuery.of(context).size.height;
        double w = MediaQuery.of(context).size.width;
        double containerHeight = isTablet ? height * 0.38 : height * 0.25;

        if(width>=600){
          return WelcomeTablet();
        }
else{
        return Container(
          color: Colors.white,
          child: Stack(
            children: [
              Positioned(
                top: 0,
                right: 0,
                left: 0,
                child: Container(
                  decoration: BoxDecoration(
                    color: Color(0xFF0B98C5),
                    borderRadius: BorderRadius.only(
                      bottomLeft: Radius.circular(30),
                      bottomRight: Radius.circular(30),
                    ),
                  ),
                  height: containerHeight,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Image.asset(
                        'assets/guardian_icon.png',
                        width: 50,
                        height: 50,
                      ),
                      SizedBox(height: isTablet ? 23 : 28),
                      Text(
                        'Welcome',
                        style: TextStyle(
                          fontFamily: "Roboto",
                          fontWeight: FontWeight.w300,
                          fontSize: isTablet ? 17 : 19,
                          color: Colors.white,
                          decoration: TextDecoration.none,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              Positioned(
                top: containerHeight + 15,
                left: 15,
                right: 35,
                child: Container(
                  height: 180,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      Expanded(
                        child: Center(
                          child: Text(
                            'Login as',
                            style: TextStyle(
                              color: Colors.black,
                              fontFamily: "Roboto",
                              fontSize: 18,
                              fontWeight: FontWeight.w300,
                              decoration: TextDecoration.none,
                            ),
                          ),
                        ),
                      ),
                      Expanded(
                        child: Material(
                          borderRadius: BorderRadius.all(Radius.circular(20)),
                          color: Colors.white,
                          elevation: 20,
                          shadowColor: const Color.fromARGB(255, 164, 164, 164),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              RadioListTile<int>(
                                activeColor: Color(0xFF0B98C5),
                                value: 1,
                                groupValue: _selectedRole,
                                onChanged: (int? value) {
                                  setState(() {
                                    _selectedRole = value!;
                                  });
                                },
                                title: Text('Nurse'),
                              ),
                              RadioListTile<int>(
                                activeColor: Color(0xFF0B98C5),
                                value: 2,
                                groupValue: _selectedRole,
                                onChanged: (int? value) {
                                  setState(() {
                                    _selectedRole = value!;
                                  });
                                },
                                title: Text('Company Admin'),
                              ),
                              RadioListTile<int>(
                                activeColor: Color(0xFF0B98C5),
                                value: 3,
                                groupValue: _selectedRole,
                                onChanged: (int? value) {
                                  setState(() {
                                    _selectedRole = value!;
                                  });
                                },
                                title: Text('Caretaker'),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              Positioned(
                top: containerHeight + 215, // Adjust top position for text fields
                left: 45,
                right: 45,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Material(
                      color: Colors.white,
                      elevation: 2,
                      borderRadius: BorderRadius.circular(15),
                      child: TextField(
                        controller: _emailController,
                        cursorColor: Color(0xFF0B98C5), // Blue cursor
                        decoration: InputDecoration(
                          labelText: 'Email',
                          labelStyle: TextStyle(
                            fontSize: 14,
                            fontFamily: "Inter",
                            color: Colors.black,
                          ),
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.all(Radius.circular(15)),
                            borderSide: BorderSide(color: Colors.black),
                          ),
                          focusedBorder: OutlineInputBorder(
                            borderSide: BorderSide(color: Colors.black), // Black border when focused
                            borderRadius: BorderRadius.all(Radius.circular(15)),
                          ),
                          enabledBorder: OutlineInputBorder(
                            borderSide: BorderSide(color: Colors.black), // Black border when not focused
                            borderRadius: BorderRadius.all(Radius.circular(15)),
                          ),
                          suffixIcon: IconButton(
                            icon: Icon(Icons.help_outline),
                            onPressed: () {
                              functionsForLogin.showHelpDialog(
                                context,
                                'Enter your email address. It should be a valid email format.',
                              );
                            },
                          ),
                        ),
                      ),
                    ),
                    SizedBox(height: 10),
                    Material(
                      color: Colors.white,
                      elevation: 2,
                      borderRadius: BorderRadius.circular(15),
                      child: TextField(
                        controller: _passwordController,
                        obscureText: true,
                        cursorColor: Color(0xFF0B98C5), // Blue cursor
                        decoration: InputDecoration(
                          labelText: 'Enter Password',
                          labelStyle: TextStyle(
                            fontSize: 14,
                            fontFamily: "Inter",
                            color: Colors.black,
                            fontWeight: FontWeight.w100
                          ),
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.all(Radius.circular(15)),
                            borderSide: BorderSide(color: Colors.black),
                          ),
                          focusedBorder: OutlineInputBorder(
                            borderSide: BorderSide(color: Colors.black), // Black border when focused
                            borderRadius: BorderRadius.all(Radius.circular(15)),
                          ),
                          enabledBorder: OutlineInputBorder(
                            borderSide: BorderSide(color: Colors.black), // Black border when not focused
                            borderRadius: BorderRadius.all(Radius.circular(15)),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              Positioned(
                top: containerHeight+370,
                left: 0,
                right: 0,
                child: Center(
                  child: TextButton(
                    style: ButtonStyle(
                      backgroundColor: MaterialStateProperty.all(
                        Color(0xFF0B98C5),
                      ),
                      padding: MaterialStateProperty.all<EdgeInsets>(
                        EdgeInsets.symmetric(
                          horizontal: w * 0.17,
                          vertical: 12,
                        ),
                      ),
                    ),
                    child: Text(
                      'Login',
                      style: TextStyle(
                        fontFamily: "Roboto",
                        fontSize: isTablet ? 17 : 15,
                        fontWeight: FontWeight.w300,
                        color: Colors.white,
                      ),
                    ),
                    onPressed: () {
                      AuthServices.signinUser(
                          _emailController.text,
                          _passwordController.text,
                          context,
                        );
                    },
                  ),
                ),
              ),
              Positioned(
                top: containerHeight+430,
                left: 0,
                right: 0,
                child: Center(
                  child: TextButton(
                    style: ButtonStyle(
                      backgroundColor: MaterialStateProperty.all(
                        Color(0xFF0B98C5),
                      ),
                      padding: MaterialStateProperty.all<EdgeInsets>(
                        EdgeInsets.symmetric(
                          horizontal: w*0.15,
                          vertical: 12,
                        ),
                      ),
                    ),
                    child: Text(
                      'Register',
                      style: TextStyle(
                        fontFamily: "Roboto",
                        fontSize: isTablet ? 17 : 14,
                        fontWeight: FontWeight.w300,
                        color: Colors.white,
                      ),
                    ),
                    onPressed: () {
                      // Call registration function here if needed
                    },
                  ),
                ),
              ),
              Positioned(
                bottom:100,
                left: 120,
                right: 120,
                child: Container(child: SignInScreen()),
              ),
              Positioned(
                bottom:10,
                left:0,
                right:0,
                child: Center(
                  child: GestureDetector(
                    onTap: (){
                      //Enter your own logic for forgot password here
                      print('Tapped');
                    },
                    child: Text('Forgot Password?',style: TextStyle(
                      fontFamily: "Inter",
                      fontSize: 12,
                      color: Color(0xFF0B98C5),
                      decoration: TextDecoration.none,
                    )),
                  ),
                ),
              ),
              Positioned(
                right: isTablet ? -10 : -10,
                bottom: -10,
                child: Image.asset(
                  'assets/finaldesign.png',
                  width: 170,
                  height: 170,
                ),
              ),
            ],
          ),
        );
}
      },
    );
  }
}
