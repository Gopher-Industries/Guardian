import 'package:flutter/material.dart';
class Functionsforlogin{
  void showHelpDialog(BuildContext context, String message) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Help'),
          content: Text(message,
          style: TextStyle(
            fontFamily: "Inter",
          ),
          ),
          actions: [
            TextButton(
              child: Text('OK',style: TextStyle(fontFamily: "Roboto",color: Colors.black,fontWeight: FontWeight.w500),),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }
}
