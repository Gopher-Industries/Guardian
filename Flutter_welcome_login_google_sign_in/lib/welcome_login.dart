// import 'package:flutter/material.dart';
// import 'package:loin_sign_in/welcome_register.dart';

// class WelcomeLogin extends StatelessWidget {
//   @override
//   Widget build(BuildContext context) {
//     return LayoutBuilder(
//       builder: (BuildContext context, BoxConstraints constraints) {
//         double width = constraints.maxWidth;
//         double height = constraints.maxHeight;
//         bool isTablet = width >= 600;
//         double h = MediaQuery.of(context).size.height;
//         double w = MediaQuery.of(context).size.width;
//         double containerHeight = isTablet ? height * 0.38 : height * 0.25;

//         return Container(
//           color: Colors.white,
//           child: Stack(
//             children: [
//               Positioned(
//                 top: 0,
//                 right: 0,
//                 left: 0,
//                 child: Container(
//                   decoration: BoxDecoration(
//                     color: Color(0xFF0B98C5),
//                     borderRadius: BorderRadius.only(
//                       bottomLeft: Radius.circular(30),
//                       bottomRight: Radius.circular(30),
//                     ),
//                   ),
//                   height: containerHeight,
//                   child: Column(
//                     mainAxisAlignment: MainAxisAlignment.center,
//                     crossAxisAlignment: CrossAxisAlignment.center,
//                     children: [
//                       Image.asset(
//                         'assets/guardian_icon.png',
//                         width: 50,
//                         height: 50,
//                       ),
//                       SizedBox(height: isTablet ? 23 : 28),
//                       Text(
//                         'Account Creation',
//                         style: TextStyle(
//                           fontFamily: "Roboto",
//                           fontWeight: FontWeight.w300,
//                           fontSize: isTablet ? 17 : 19,
//                           color: Colors.white,
//                           decoration: TextDecoration.none,
//                         ),
//                       ),
//                     ],
//                   ),
//                 ),
//               ),
//               Positioned(
//                 top: containerHeight + 20,
//                 left: 0,
//                 right: 0,
//                 child: Image.asset(
//                   'assets/help.png',
//                   width: 110,
//                   height: 110,
//                 ),
//               ),
//               Positioned(
//                 top: containerHeight + 140, // Adjust top position for text fields
//                 left: 45,
//                 right: 45,
//                 child: Column(
//                   crossAxisAlignment: CrossAxisAlignment.stretch,
//                   children: [
//                     Material(
//                       color: Colors.white,
//                       elevation: 2,
//                       borderRadius: BorderRadius.circular(15),
//                       child: TextField(
//                         decoration: InputDecoration(
//                           labelText: 'Enter Email Address',
//                           labelStyle: TextStyle(fontSize: 14, fontFamily: "Inter"),
//                           border: OutlineInputBorder(
//                             borderRadius: BorderRadius.all(Radius.circular(15)),
//                           ),
//                           suffixIcon: IconButton(
//                             icon: Icon(Icons.help_outline),
//                             onPressed: () {
//                               _showHelpDialog(
//                                 context,
//                                 'Enter your email address. It should be a valid email format.',
//                               );
//                             },
//                           ),
//                         ),
//                       ),
//                     ),
//                     SizedBox(height: 10),
//                     Material(
//                       color: Colors.white,
//                       elevation: 2,
//                       borderRadius: BorderRadius.circular(15),
//                       child: TextField(
//                         obscureText: true,
//                         decoration: InputDecoration(
//                           labelText: 'Enter Password',
//                           labelStyle: TextStyle(fontSize: 14, fontFamily: "Inter"),
//                           border: OutlineInputBorder(
//                             borderRadius: BorderRadius.all(Radius.circular(15)),
//                             borderSide: BorderSide(
//                               color: Color(0xFF0B98C5),
//                             ),
//                           ),
//                           suffixIcon: IconButton(
//                             icon: Icon(Icons.help_outline),
//                             onPressed: () {
//                               _showHelpDialog(
//                                 context,
//                                 'Enter your password. Make sure it is secure and includes letters, numbers, and symbols.',
//                               );
//                             },
//                           ),
//                         ),
//                       ),
//                     ),
//                     SizedBox(height: 10),
//                     Material(
//                       color: Colors.white,
//                       elevation: 2,
//                       borderRadius: BorderRadius.circular(15),
//                       child: TextField(
//                         obscureText: true,
//                         decoration: InputDecoration(
//                           labelText: 'Confirm Password',
//                           labelStyle: TextStyle(fontSize: 14, fontFamily: "Inter"),
//                           border: OutlineInputBorder(
//                             borderRadius: BorderRadius.all(Radius.circular(15)),
//                             borderSide: BorderSide(
//                               color: Color(0xFF0B98C5),
//                             ),
//                           ),
//                         ),
//                       ),
//                     ),
//                   ],
//                 ),
//               ),
//               Positioned(
//                 bottom: 130,
//                 left: 0,
//                 right: 0,
//                 child: Center(
//                   child: TextButton(
//                     style: ButtonStyle(
//                       backgroundColor: MaterialStateProperty.all(
//                         Color(0xFF0B98C5),
//                       ),
//                       padding: MaterialStateProperty.all<EdgeInsets>(
//                         EdgeInsets.symmetric(
//                           horizontal: w * 0.14,
//                           vertical: 12,
//                         ),
//                       ),
//                     ),
//                     child: Text(
//                       'Create Account',
//                       style: TextStyle(
//                         fontFamily: "Roboto",
//                         fontSize: isTablet ? 17 : 15,
//                         fontWeight: FontWeight.w300,
//                         color: Colors.white,
//                       ),
//                     ),
//                     onPressed: () {
//                       // Navigate to the page after logging in
//                       // Navigator.push(
//                       //   context,
//                       //   MaterialPageRoute(
//                       //     builder: (context) => NextScreen(), // Replace with your target screen widget
//                       //   ),
//                       // );
//                     },
//                   ),
//                 ),
//               ),
//               Positioned(
//                 bottom: 75,
//                 left: 0,
//                 right: 0,
//                 child: Center(
//                   child: TextButton(
//                     style: ButtonStyle(
//                       backgroundColor: MaterialStateProperty.all(
//                         Color(0xFF0B98C5),
//                       ),
//                       padding: MaterialStateProperty.all<EdgeInsets>(
//                         EdgeInsets.symmetric(
//                           horizontal: w * 0.15,
//                           vertical: 12,
//                         ),
//                       ),
//                     ),
//                     child: Text(
//                       'Back to Login',
//                       style: TextStyle(
//                         fontFamily: "Roboto",
//                         fontSize: isTablet ? 17 : 15,
//                         fontWeight: FontWeight.w300,
//                         color: Colors.white,
//                       ),
//                     ),
//                     onPressed: () {
//                       // Navigate to the page after registering
//                       // Navigator.push(
//                       //   context,
//                       //   MaterialPageRoute(
//                       //     builder: (context) => Welcome(), // Replace with your target screen widget
//                       //   ),
//                       // );
//                     },
//                   ),
//                 ),
//               ),
//               Positioned(
//                 right: isTablet ? -10 : -10,
//                 bottom: -10,
//                 child: Image.asset(
//                   'assets/finaldesign.png',
//                   width: 170,
//                   height: 170,
//                 ),
//               ),
//             ],
//           ),
//         );
//       },
//     );
//   }

//   void _showHelpDialog(BuildContext context, String message) {
//     showDialog(
//       context: context,
//       builder: (BuildContext context) {
//         return AlertDialog(
//           title: Text('Help'),
//           content: Text(message,
//           style: TextStyle(
//             fontFamily: "Inter",
//           ),
//           ),
//           actions: [
//             TextButton(
//               child: Text('OK'),
//               onPressed: () {
//                 Navigator.of(context).pop();
//               },
//             ),
//           ],
//         );
//       },
//     );
//   }
// }
