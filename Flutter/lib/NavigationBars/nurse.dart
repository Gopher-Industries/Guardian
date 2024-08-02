import 'package:flutter/material.dart';

class Nurse extends StatefulWidget {
  @override
  _NurseState createState() => _NurseState();
}

class _NurseState extends State<Nurse> {
  PageController _pageControllerNurse = PageController();
  int _currentIndex = 0;

  @override
  void initState() {
    super.initState();
    _pageControllerNurse.addListener(() {
      setState(() {
        _currentIndex = _pageControllerNurse.page!.round();
      });
    });
  }

  void _onItemTapped(int index) {
    setState(() {
      _currentIndex = index;
    });
    _pageControllerNurse.jumpToPage(index);
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        PageView(
          controller: _pageControllerNurse,
          //Modify the below code to navigate to actual pages from the navigation bar
          children: [
            Center(child: Text('Activity')),
            Center(child: Text('Patient List')),
            Center(child: Text('Calendar')),
          ],
        ),
        Positioned(
          top: MediaQuery.of(context).size.height-70,
          bottom: 0,
          left: 0,
          right: 0,
          child: Container(
            decoration: BoxDecoration(
              color: Color.fromARGB(255, 56, 188, 249),
              borderRadius: BorderRadius.only(
                topLeft: Radius.circular(12.0),
                topRight: Radius.circular(12.0),
              ),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: <Widget>[
                _buildNavItem(Icons.local_activity, 'Activity', 0),
                _buildNavItem(Icons.contact_page, 'Patient List', 1),
                _buildNavItem(Icons.calendar_today, 'Calendar', 2),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildNavItem(IconData icon, String label, int index) {
    return Expanded(
      child: InkWell(
        onTap: () => _onItemTapped(index),
        child: Container(
          decoration: BoxDecoration(
            borderRadius: BorderRadius.all(
              Radius.circular(12.0),
            ),
            color: _currentIndex == index ? Color.fromARGB(255, 174, 215, 248) : Colors.transparent,
          ),
          alignment: Alignment.center,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                icon,
                color: _currentIndex == index ? Colors.lightBlue : Colors.white,
              ),
              SizedBox(height:5),
              Text(
                label,
                style: TextStyle(
                  fontFamily: 'Roboto',
                  fontSize: 16,
                  fontWeight: FontWeight.w100,
                  color: _currentIndex == index ? Colors.lightBlue : Colors.white,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
