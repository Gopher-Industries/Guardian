import 'package:flutter/material.dart';
import 'package:loin_sign_in/functions/googlesignin.dart';
 // Import the file containing the Authentication class

class SignInScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
          style: TextButton.styleFrom(
            // primary: Colors.black, // Text color
            backgroundColor: Colors.white, // Background color
            elevation: 10,
            // padding: EdgeInsets.symmetric(horizontal: 20, vertical: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(0),
            ),
          ),
          onPressed: () {
            Authentication.signInWithGoogle(context: context);
          },
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Image.asset(
                'assets/google_logo.png', // Path to Google logo asset
                height: 24,
              ),
              SizedBox(width: 10),
              Text(
                'Sign in',
                style: TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.w400,
                  fontFamily: "Roboto",
                  color: Colors.black,
                ),
              ),
            ],
          ),
        );
  }
}
