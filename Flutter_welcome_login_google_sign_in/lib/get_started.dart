import 'package:flutter/material.dart';
import 'package:loin_sign_in/googlesigninbutton.dart';
import 'package:loin_sign_in/welcomeLogin.dart';
import 'package:loin_sign_in/welcomeLoginTablet.dart';


class GetStarted extends StatelessWidget {
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
                      Image.asset('assets/guardian_icon.png',
                      width: 50,
                      height: 50,
                      ),
                      SizedBox(height: isTablet ? 23 : 28),
                      Text(
                        'Welcome to Patient Checker',
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
                top: containerHeight + 20,
                left: 0,
                right: 0,
                child: Image.asset('assets/help.png',
                width: isTablet ? 90 : 110,
                height: isTablet ? 90 : 110,
                ),
              ),
              Positioned(
                top: isTablet ? containerHeight + 120 : containerHeight + 160,
                left: 0,
                right: 0,
                child: Center(
                  child: Text(
                    'Take care of your patients',
                    style: TextStyle(
                      fontFamily: "Inter",
                      fontSize: isTablet ? 15 : 15,
                      fontWeight: FontWeight.w100,
                      decoration: TextDecoration.none,
                      color: const Color.fromARGB(255, 90, 90, 90),
                    ),
                  ),
                ),
              ),
              Positioned(
                bottom: isTablet ? 20 : 150, // Adjusted for better alignment
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
                          horizontal: w * 0.09, // Responsive horizontal padding
                          //vertical: isTablet ? 16 : 12, // Responsive vertical padding
                        ),
                      ),
                    ),
                    child: Text(
                      'Get Started',
                      style: TextStyle(
                        fontFamily: "Roboto",
                        fontSize: isTablet ? 17 : 15,
                        fontWeight: FontWeight.w300,
                        color: Colors.white,
                      ),
                    ),
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          // builder: (context)=> SignInScreen()
                          builder: (context) => isTablet? WelcomeTablet(): Welcome(), // Replace NextScreen with your target screen widget
                        ),
                      );
                    },
                  ),
                ),
              ),
              Positioned(
                right:isTablet? -10: -10,
                bottom: -10,
                child: Image.asset('assets/finaldesign.png',
                width: isTablet? 130: 170,
                height: isTablet? 140: 170,
                ),
              ),
            ],
          ),
        );
      },
    );
  }
}


