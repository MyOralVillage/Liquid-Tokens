const functions = require('firebase-functions');
const admin = require('firebase-admin');
const path = require('path');
const { Storage } = require('@google-cloud/storage');
const spawn = require('child-process-promise').spawn;
const os = require('os');
const fs = require('fs');

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
                destination: path.join(path.dirname(filePath), 'profile.jpg'),
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

    return app.database().ref('users').child(user.uid).update({
        balance: 0,
        image: '/users/profile.jpg'
    });
});
