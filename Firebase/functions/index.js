const functions = require('firebase-functions');
const admin = require('firebase-admin');

const app = admin.initializeApp();

exports.createUser = functions.auth.user().onCreate((user) => {

    return app.database().ref('users').child(user.uid).update({
        balance: 0
    });
});
