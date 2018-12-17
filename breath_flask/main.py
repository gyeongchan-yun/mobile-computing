from flask import Flask, request

import numpy as np
import librosa
from sklearn import linear_model, model_selection

import os
import random


app = Flask(__name__)

app.config['UPLOAD_FOLDER'] = './upload'
app.config['HOST'] = ''  # put host here

file_offset = 0
threshold = 9
normal_feature, feature_set, classification = [], [], []


@app.route('/')
def hello_world():
    return 'Hello World!'


def extract_feature(file_name):
    print("file name: ", file_name)
    X, sample_rate = librosa.load(file_name,mono=True)
    print("audio series's num", X.shape, "samplerate", sample_rate)
    mfcc = librosa.feature.mfcc(y=X,
                                sr=sample_rate,
                                hop_length=int(sample_rate*0.01),
                                n_fft=int(sample_rate*0.02),
                                n_mfcc=30).T

    print("mfcc.shape: ", mfcc.shape)
    print("mfcc vector: ", mfcc)
    mfcc = mfcc.mean(axis=0) # 1 x 30 array
    print("mfcc vector after mean: ", mfcc)
    return mfcc


def generate_dataset(feature):
    global feature_set, classification
    for _ in range(0, 100):
        abnormal_feature = feature * random.randrange(1, threshold) / 10
        feature_set.append(abnormal_feature)
        classification.append(0)
        feature_set.append(feature)
        classification.append(1)

    X = np.vstack(feature_set)
    Y = np.array(classification).T

    return X, Y


def expand_dataset(feature_set, classification, normal, feature):
    X, Y = [], []
    diff_rate = (feature / normal).mean()

    feature_set.append(feature)
    if diff_rate > 1: # faster rate
        classification.append(1)
        diff_rate = (diff_rate - 1) * 100
    else:
        classification.append(0)
        diff_rate = (1 - diff_rate) * 100

    X = np.vstack(feature_set)
    Y = np.array(classification).T

    return diff_rate, X, Y


@app.route('/get_file', methods=['GET', 'POST'])
def get_file():
    if request.method == 'POST':
        file = request.files['file']
        if file:
            global file_offset, normal_feature, X, Y

            file.filename = file.filename.split('.')[0] + str(file_offset) + '.' + file.filename.split('.')[1]
            print("file name: ", file.filename)
            audio_file = os.path.join(app.config['UPLOAD_FOLDER'], file.filename)
            file.save(audio_file)
            feature = extract_feature(audio_file)

            diff_rate = 0
            if file_offset == 0:
                normal_feature = feature
                X, Y = generate_dataset(feature)
            else:
                diff_rate, X, Y = expand_dataset(feature_set, classification, normal_feature, feature)

            logistic_regression = linear_model.LogisticRegression()
            logistic_regression.fit(X, Y)
            Y_predict = logistic_regression.predict(X)
            Y_predict_prob = logistic_regression.predict_proba(X)

            if file_offset == 0:
                result = str(file_offset) + ',' + 'Register your first breath!'
            else:
                if Y_predict[-1] == 1:
                    prob = str(Y_predict_prob[-1][-1])
                else:
                    prob = str(Y_predict_prob[-1][0])
                result = str(file_offset) + ',' + str(diff_rate) + ',' + str(Y_predict[-1]) + ',' + prob

            file_offset += 1

            return str(result)

    return '''
    <!doctype html>
    <title>Server</title>
    <h1>Upload new File</h1>
    '''


if __name__ == '__main__':
    app.run(host=app.config['HOST'], debug=True)