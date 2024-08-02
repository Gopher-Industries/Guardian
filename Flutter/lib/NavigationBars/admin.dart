import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class Admin extends StatefulWidget {
  @override
  _AdminState createState() => _AdminState();
}

class _AdminState extends State<Admin> {
  PageController _pageControllerAdmin = PageController();
  int _currentIndex = 0;

  @override
  void initState() {
    super.initState();
    _pageControllerAdmin.addListener(() {
      setState(() {
        _currentIndex = _pageControllerAdmin.page!.round();
      });
    });
  }

  void _onItemTapped(int index) {
    setState(() {
      _currentIndex = index;
    });
    _pageControllerAdmin.jumpToPage(index);
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        PageView(
          controller: _pageControllerAdmin,
          //Modify the below code to navigate to actual pages from the navigation bar
          children: [
            Center(child: Text('Patient Page', style: GoogleFonts.roboto(fontSize: 24))),
            Center(child: Text('Next of Kin Page', style: GoogleFonts.roboto(fontSize: 24))),
            Center(child: Text('General Practitioner Page', style: GoogleFonts.roboto(fontSize: 24))),
            Center(child: Text('Health Details Page', style: GoogleFonts.roboto(fontSize: 24))),
            Center(child: Text('Health & Welfare Det. Page', style: GoogleFonts.roboto(fontSize: 24))),
          ],
        ),
        Positioned(
          bottom: 0,
          left: 0,
          right: 0,
          child: BottomAppBar(
            child: SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: <Widget>[
                  _buildNavItem('Patient', null, 0),
                  _buildNavItem('Next of Kin', null, 1),
                  _buildNavItem('General', 'Practitioner', 2),
                  _buildNavItem('Health', 'Details', 3),
                  _buildNavItem('Health &', 'Welfare Det.', 4),
                ],
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildNavItem(String label1, String? label2, int index) {
    double width = MediaQuery.of(context).size.width;

    return InkWell(
      onTap: () => _onItemTapped(index),
      child: Container(
        padding: EdgeInsets.symmetric(horizontal: width / 12),
        alignment: Alignment.center,
        child: Stack(
          children: [
            Align(
              alignment: Alignment.center,
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    label1,
                    style: TextStyle(
                      fontFamily: 'Roboto',
                      fontSize: 16.5,
                      fontWeight: _currentIndex == index ? FontWeight.w300 : FontWeight.normal,
                      color: _currentIndex == index ? Colors.blue : Color.fromARGB(255, 117, 117, 117),
                    ),
                  ),
                  if (label2 != null && (index == 2 || index == 3 || index == 4))
                    Text(
                      label2,
                      style: TextStyle(
                        fontFamily: 'Roboto',
                        fontSize: 16.5,
                        fontWeight: _currentIndex == index ? FontWeight.w300 : FontWeight.normal,
                        color: _currentIndex == index ? Colors.blue : Color.fromARGB(255, 117, 117, 117),
                      ),
                    ),
                ],
              ),
            ),
            if (_currentIndex == index)
              Positioned(
                bottom: 0,
                left: 0,
                right: 0,
                child: Container(
                  height: 2.5,
                  color: Colors.blue,
                ),
              ),
          ],
        ),
      ),
    );
  }
}
