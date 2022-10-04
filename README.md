# Work Allocation and Time Management System

An accompanying repo for the IUMW IAC 2021 conference paper titled _Work Allocation and Time Management System_<span title="see footnote on confrence paper with complete introduction">[^1]</span>. This Android application implements the <span title="Activity, Length, Planning, Establishment of Priorities, Notation">ALPEN</span> 
time management framework.

<img src="https://user-images.githubusercontent.com/67423428/193908108-00b984d6-20f3-4699-873a-6ef62e5729e5.png" height="400"/>


## Implementing The ALPEN _Plan Time_ and _Extra Time_
 The core functionality of the ALPEN procedure begins by creating references corresponding to GUI elements such as buttons, selectors, and text fields for specifying task preferences.
 
The collected inputs are sent for processing, starting with the evaluation of task duration by calculating time difference and representing the value in long data type. Four extra variables are created to convert the duration to seconds, minutes, hours, and days format, all stored as double data type.

This is immediately followed by planning duration (60% of the initial duration) and extra time duration (40%) for each of all four variables. From here, three date objects corresponding to the planning, extra, and initial durations are created in order to allow the user(s) being assigned the tasks to locally set alarms within the program. 

Prior to registering the task being assigned, the credentials of all associated users (the creator of the task, the person assigned to the task, and an optional collaborator) are subsequently verified via name and email variables.

With exception handling and input validation
mechanisms in place, and all ALPEN related properties identified, the necessary values are then sent to the Tasks and Notifications nodes in the Firebase Realtime Database, effectively registering the creation of a task governed by the ALPEN framework. The behavioural model below illustrates this routine.

![image](https://user-images.githubusercontent.com/67423428/193907672-4e88c560-373f-482b-b14c-24df3abc4e93.png)

### Prerequisites

- **Java 8**: At the moment of writing, the source code was written with Java 8. Should you wish to use a higher/up-to-date version, be sure to account for compatibility makeshifts.
- **A.S. 3.5**: Be wary of compatibility issues for a new version of Android Studio.
- **Gradle**: Model - 3.5.3; Build - 5.4.1.
- **Firebase**: Auth:19.3.0 /Core: 17.2.3/Database: 19.2.1/UI: 6.2.0.
- **Google Play Services**: 4.3.3
- **API Level 23** (modified when needed).
- **SDK Version 23** (modified when needed).

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## References

* **Paul Deitel** - [Android 6 For Programmers 3e](https://github.com/pdeitel/AndroidHowToProgram3e)
* **Ted Hagos** - [Learn Android Studio 3](https://github.com/Apress/learn-android-studio-)
* **Laurence Moroney** - [The Definitive Guide To Firebase](https://github.com/Apress/def-guide-to-firebase)
* **Onur Cinar** - [Android Quick APIs Reference](https://g.co/kgs/A5RfbX)
* **Neil Smyth** - [Android Studio 3 Essentials](https://www.techotopia.com/index.php/Android_Studio_Development_Essentials_-_Android_6_Edition)

## License

This project is licensed under the [GNU GPL v3](https://choosealicense.com/licenses/gpl-3.0/) License.

## Acknowledgments

* Hat tip to anyone whose code was used

<!-- Footnotes formatted by GitHub to appear here -->

[^1]: Mohammed, A., & Selvarajah, C.S. (2022). Work Allocation and Time Management System.
In N. N. Omar, N. S. Choo, N. Jayaram, A. A. Rachman, & H. Hassan (Eds.), 
<i>Proceedings of the 1st International Academic Conference 2021</i> (pp. 159-165). 
Kuala Lumpur: International University of Malaya-Wales (IUMW). Retrieved: https://www.iumw.edu.my/wp-content/uploads/2022/06/IUMW-IAC-2021-Proceedings.pdf
