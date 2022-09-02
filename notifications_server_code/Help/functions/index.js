'use strict'


const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendHelp = functions.database.ref('/help/{receiver_user_id}/{notification_id}')
.onWrite((data, context) =>
{
 	const receiver_user_id = context.params.receiver_user_id;
 	const notification_id = context.params.notification_id;


 	console.log('We have a notification to send to : ', receiver_user_id);

 	if (!data.after.val()) 
 	{
 		console.log('A notification has been deleted : ', notification_id);
 		return null;
 	}




	const task_title = admin.database().ref(`/help/${receiver_user_id}/${notification_id}/taskTitle`).once('value');

	return task_title.then(fromTaskResult => 

		{
			const from_task_title = fromTaskResult.val();

			console.log('The name of the task is : ', task_title);



			 	const sender_user_id = admin.database().ref(`/help/${receiver_user_id}/${notification_id}`).once('value');

			 	return sender_user_id.then(fromUserResult => 
			 		{
			 			const from_sender_user_id = fromUserResult.val().from;

			 			console.log('You have a notification from : ', sender_user_id);

			 			const userQuery = admin.database().ref(`/users/${from_sender_user_id}/name`).once('value');

			 			return userQuery.then(userResult => 
						{
							const senderUserName = userResult.val();
						

							const DeviceToken = admin.database().ref(`/users/${receiver_user_id}/deviceToken`).once('value');

						 	return DeviceToken.then(result =>
						 	 {
						 	 	const token_id = result.val();

						 	 	const payload =
						 	 	{
						 	 		notification:
						 	 		{
						 	 			from_sender_user_id : from_sender_user_id, 
						 	 			title: "Someone Needs Help With A Task",
						 	 			body: `${senderUserName} Is Asking For Help On A Task (${from_task_title}).`,
						 	 			icon: "default"
						 	 		}
						 	 	};

						 	 	return admin.messaging().sendToDevice(token_id, payload)
						 	 	.then(response => 
						 	 		{
						 	 			console.log('This was a notification feature.')
						 	 		});
						 	 });

						});	

			 		});




	 });




});
