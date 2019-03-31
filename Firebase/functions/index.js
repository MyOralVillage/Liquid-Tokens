const functions = require('firebase-functions');
const admin = require('firebase-admin');
const path = require('path');
const { Storage } = require('@google-cloud/storage');
const spawn = require('child-process-promise').spawn;
const os = require('os');
const fs = require('fs');

const qr = require('qrcode');

const gcs = new Storage();

const app = admin.initializeApp();

exports.profileImage = functions.storage.object().onFinalize((object, context) => {
    
    const filePath = object.name;
    const fileName = path.basename(filePath);
    const fileBucket = object.bucket;
    const fileContentType = object.contentType;

    const uid = path.basename(path.dirname(filePath));

    if(fileName.startsWith('raw_')) {
        const bucket = gcs.bucket(fileBucket);
        const tempFilePath = path.join(os.tmpdir(), fileName);
        const metadata = {
            contentType: fileContentType,
        };

        return bucket.getFiles().then((result) => {

            return bucket.file(filePath).download({
                destination: tempFilePath
            });
        }).then(() => {
            return spawn('convert', [tempFilePath, '-geometry', '512x', tempFilePath]);
        }).then(() => {

            return bucket.upload(tempFilePath, {
                destination: path.join(path.dirname(filePath), fileName.split('raw_')[1]),
                metadata: metadata
            });
        }).then((storagePath) => {
            fs.unlinkSync(tempFilePath);

            return app.database().ref('users').child(uid).update({
                image: storagePath[0].name
            });
        }).catch((e) => {
            console.log(e);
        })
    }

    return Promise.resolve(null);
});

exports.createUser = functions.auth.user().onCreate((user) => {
    const qrFilePath = path.join(os.tmpdir(), 'qr.png');
    return qr.toFile(qrFilePath, user.uid).then(() => {
        const bucket = gcs.bucket('my-oral-village-app.appspot.com');

        return bucket.upload(qrFilePath, {
            destination: path.join('users', user.uid, 'qr.png'),
        });
    }).then((storagePath) => {
        fs.unlinkSync(qrFilePath);

        return app.database().ref('users').child(user.uid).update({
            balance: 10000,
            uid: user.uid,
            image: '/users/profile.jpg',
            qr: storagePath[0].name
        });
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
            currency: "usd",
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
            currency: "usd",
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