const functions = require('firebase-functions');
const admin = require('firebase-admin');

const app = admin.initializeApp();

exports.createUser = functions.auth.user().onCreate((user) => {

    return app.database().ref('users').child(user.uid).update({
        balance: 0,
        uid: user.uid
    });
});

exports.transaction = functions.https.onCall((data, context) => {   
    if(context.auth === undefined) {
        throw new functions.https.HttpsError('permission-denied', 'invalid auth');
    }
    const { amount, to } = data;
    const uid = context.auth.uid;

    return app.database().ref('users').child(to).once("value").then((value) => {
        if(!value.exists() || amount <= 0) {
            throw new functions.https.HttpsError('invalid-argument', 'invalid amount or to');
        }

        return app.database().ref('transactions').push({
            from: uid,
            to: to,
            amount: amount,
            currency: "cad",
            time: (new Date()).getTime(),
        });
    }).then(() => {
        return Promise.all([app.database().ref('users').child(uid).child("balance").transaction((data) => {
            return data - amount;
        }), app.database().ref('users').child(to).child("balance").transaction((data) => {
            return data + amount;
        })]);
    }).then(() => {
        return {
            result: 'success',
        };
    }).catch((error) => {
        throw new functions.https.HttpsError('unknown', error);
    })
});

exports.request = functions.https.onCall((data, context) => {
    if(context.auth === undefined) {
        throw new functions.https.HttpsError('permission-denied', 'invalid auth');
    }
    const { amount, to } = data;
    const uid = context.auth.uid;

    return app.database().ref('users').child(to).once("value").then((value) => {
        if(!value.exists() || amount <= 0) {
            throw new functions.https.HttpsError('invalid-argument', 'invalid amount or to');
        }

        return app.database().ref('requests').push({
            from: uid,
            to: to,
            status: 'pending',
            amount: amount,
            currency: "cad",
            time: (new Date()).getTime(),
        });
    }).then(() => {
        return {
            result: 'success',
        };
    }).catch((error) => {
        throw new functions.https.HttpsError('unknown', error);
    })
});