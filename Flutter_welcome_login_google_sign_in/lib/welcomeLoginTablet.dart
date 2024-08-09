import 'package:flutter/material.dart';
import 'package:loin_sign_in/functions/FunctionsForLogin.dart';
import 'package:loin_sign_in/googlesigninbutton.dart';
import 'package:loin_sign_in/welcomeLogin.dart';

class WelcomeTablet extends StatefulWidget {
  @override
  _WelcomeTabletState createState() => _WelcomeTabletState();
}

class _WelcomeTabletState extends State<WelcomeTablet> {
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
        double containerHeight = height * 0.38;

        if (width < 600) {
          return Welcome(); // Recursive call, needs adjustment or removal
        } else {
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
                  top: containerHeight + 10,
                  bottom: 0,
                  right: 20,
                  left: 15,
                  child: Container(
                    //color: Colors.red,
                    padding: EdgeInsets.all(5),
                    child: Row(
                      children: [
                        Container(
                          //color: Colors.yellow,
                          width: width * 0.5,
                          child: Row(
                            children: [
                              Text(
                                'Login as',
                                style: TextStyle(
                                  color: Colors.black,
                                  fontFamily: "Roboto",
                                  fontSize: 18,
                                  fontWeight: FontWeight.w300,
                                  decoration: TextDecoration.none,
                                ),
                              ),
                              SizedBox(width: 40),
                              Container(
                                width: width * 0.3,
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
                        SizedBox(width: 20),
                        Expanded(
                          child: Container(
                            padding: EdgeInsets.all(0),
                            height: height - containerHeight - 20,
                            //color: Colors.green,
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.center,
                              children: [
                                Material(
                                  color: Colors.white,
                                  elevation: 2,
                                  borderRadius: BorderRadius.circular(15),
                                  child: TextField(
                                    controller: _emailController,
                                    cursorColor: Color(0xFF0B98C5),
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
                                        borderSide: BorderSide(color: Colors.black),
                                        borderRadius: BorderRadius.all(Radius.circular(15)),
                                      ),
                                      enabledBorder: OutlineInputBorder(
                                        borderSide: BorderSide(color: Colors.black),
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
                                    cursorColor: Color(0xFF0B98C5),
                                    decoration: InputDecoration(
                                      labelText: 'Enter Password',
                                      labelStyle: TextStyle(
                                        fontSize: 14,
                                        fontFamily: "Inter",
                                        color: Colors.black,
                                        fontWeight: FontWeight.w100,
                                      ),
                                      border: OutlineInputBorder(
                                        borderRadius: BorderRadius.all(Radius.circular(15)),
                                        borderSide: BorderSide(color: Colors.black),
                                      ),
                                      focusedBorder: OutlineInputBorder(
                                        borderSide: BorderSide(color: Colors.black),
                                        borderRadius: BorderRadius.all(Radius.circular(15)),
                                      ),
                                      enabledBorder: OutlineInputBorder(
                                        borderSide: BorderSide(color: Colors.black),
                                        borderRadius: BorderRadius.all(Radius.circular(15)),
                                      ),
                                    ),
                                  ),
                                ),
                                SizedBox(height: 10),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    TextButton(
                                      onPressed: () {
                                        // Add your login logic here
                                      },
                                      child: Text(
                                        'Login',
                                        style: TextStyle(
                                          color: Colors.white,
                                          fontSize: 14,
                                        ),
                                      ),
                                      style: TextButton.styleFrom(
                                        backgroundColor: Color(0xFF0B98C5), // Button color
                                        padding: EdgeInsets.symmetric(vertical: 6, horizontal: 40),
                                        shape: RoundedRectangleBorder(
                                          borderRadius: BorderRadius.circular(15),
                                        ),
                                      ),
                                    ),
                                    SizedBox(height: 10),
                                    TextButton(
                                      onPressed: () {
                                        // Add your registration logic here
                                      },
                                      child: Text(
                                        'Register',
                                        style: TextStyle(
                                          color: Colors.white,
                                          fontSize: 14,
                                        ),
                                      ),
                                      style: TextButton.styleFrom(
                                        backgroundColor: Color(0xFF0B98C5), // Button color
                                        padding: EdgeInsets.symmetric(vertical: 6, horizontal: 33),
                                        shape: RoundedRectangleBorder(
                                          borderRadius: BorderRadius.circular(15),
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                                SizedBox(height:5),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    Container(height:30, width:130, child: SignInScreen()),
                                    GestureDetector(
                    onTap: (){
                      //Enter your own logic for forgot password here
                      print('Tapped');
                    },
                    child: Text('Forgot Password?',style: TextStyle(
                      fontFamily: "Inter",
                      fontSize: 14,
                      color: Color(0xFF0B98C5),
                      decoration: TextDecoration.none,
                    )),
                  ),
                                  ],
                                )
                                
                              ],
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                Positioned(
                right: isTablet ? -10 : -10,
                bottom: -10,
                child: Image.asset(
                  'assets/finaldesign.png',
                  width: 130,
                  height: 130,
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
