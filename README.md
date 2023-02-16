# Calendar

## Cloning Samsung Calendar and add features

- Samsung Electronics is trying to build an Internet of Things (IoT) infrastructure, and we also thought these were important. </br>
And We decided that the Samsung Calendar application was the center and started the cloning project.</br>
And We were able to create an upgraded Samsung calendar by adding ideas that We thought were necessary from the user's point of view.

## Demonstration

### First Screen
[video1.webm](https://user-images.githubusercontent.com/75816070/219357315-566441d8-ec63-4399-a7b5-fdb713d7e857.webm)

</br>
</br>
- This screen was designed by cloning the Samsung Calendar app as much as possible.</br>
At first, we tried to produce it using a material calendar view, but we couldn't implement a size-adjusting animation, so we created our own custom calendar view.</br>
A calendar view to which size adjustment animation and horizontal slide animation can be applied was produced by mixing view pager and recycler view.

### Second Screen
[video2.webm](https://user-images.githubusercontent.com/75816070/219360533-114c7c5b-3358-4f77-bead-a8c7c1b954b9.webm)

</br>
</br>
- And, we cloned Samsung Calendar's Scheduling Feature.</br>
We used date picker and time picker.</br>
We also developed features like reminder and alarm.

### New Feature in Second Screen : Before Departing Alarm System
[video3.webm](https://user-images.githubusercontent.com/75816070/219361715-55880548-8d27-4a51-91a6-64762d6f6014.webm)

</br>
</br>
- In previous task, we added schedules which is repeated every week and its name is "제목".</br>
And we can see those schedules in this video at first.<br>
By the way, One of our team mates used not to show up on time.<br>
So we came up with a good idea : Predict the time it takes to get from the departure point to the destination, and notify the user at the scheduled departure time.<br>
If you initially agree to provide location information, the departure point is set to the current location.<br>
After that, the origin and destination can be set through the web view. <br>
The departure and destination locations are displayed on Google Maps, and when you select a means of transportation and click a button, the time required and the scheduled departure time are displayed. <br>
This information is stored in the database when the schedule is stored.

### Alarm Feature
[video4.webm](https://user-images.githubusercontent.com/75816070/219367860-da01b663-03d9-461e-8bfd-65e5ee73c1ea.webm)

</br>
</br>
- A notification is displayed at the time set in the previous section. <br>
This was implemented through Foreground Service and Thread. <br>
If rain or snow is forecast at the time the alarm is displayed, it is recommended to take an umbrella at the same time as the schedule is notified.

### Database
[video5.webm](https://user-images.githubusercontent.com/75816070/219370701-a9f176dd-3a9c-4e64-901d-f5d4619c7655.webm)

</br>
</br>
- We built a database using local DB and firebase. <br> In addition, the user can edit and delete the stored schedule directly through the recycler view.

### Navigation View & Log-in & Sign-up
[video6.webm](https://user-images.githubusercontent.com/75816070/219398384-59f4c5bb-8b84-4beb-a738-8f17e1e045e1.webm)

</br>
</br>
- The menu was made accessible using Navigation View. </br>
In the menu, you can decide which DB to use and how to set the alarm.</br>
And if you press the login button, you can go to the login screen and the membership screen connected to the Firebase.

## Closing

We were able to acquire a lot of app development knowledge while working on the cloning project.</br>
However, there were some parts that we could not accurately implement, such as weekly daily calendars and soft animations. </br>
Still, we want to focus on the fact that we were able to upgrade our existing high-quality applications. </br>
Thank you for finding it :)
