import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class CareTaker extends StatefulWidget {
  @override
  _CareTakerState createState() => _CareTakerState();
}

class _CareTakerState extends State<CareTaker> {
  PageController _pageControllerCareTaker = PageController();
  int _currentIndex = 0;

  @override
  void initState() {
    super.initState();
    _pageControllerCareTaker.addListener(() {
      setState(() {
        _currentIndex = _pageControllerCareTaker.page!.round();
      });
    });
  }

  void _onItemTapped(int index) {
    setState(() {
      _currentIndex = index;
    });
    _pageControllerCareTaker.jumpToPage(index);
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        PageView(
          controller: _pageControllerCareTaker,
          //Modify the below code to navigate to actual pages from the navigation bar
          children: [
            Center(child: Text('Patient')),
            Center(child: Text('Next Of Kin')),
            Center(child: Text('General Practictioner')),
            Center(child: Text('Health Details')),
            Center(child: Text('Health And Welfare Det.')),
            Center(child: Text('Care Plan')),
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
                  _buildNavItem('Care Plan', null, 5),
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
