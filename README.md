# Streamlining Property Rentals Android Application

## Overview

This repository contains the source code for an Android application designed to streamline property rentals in the Indian market. The application facilitates property listing and browsing, integrates with Google Firebase Firestore for backend data storage, and uses a machine learning model to predict rental prices.

## Features

- **User Authentication**: Secure user registration and login.
- **Property Listing**: Allows property owners and brokers to list their properties with details such as number of rooms, area size, locality, and images.
- **Property Browsing**: Enables tenants to browse through listed properties, filter based on criteria, and view detailed information.
- **Contact Property Incharge**: Tenants can contact the property incharge directly through the app.
- **Rent Prediction**: Machine learning model integrated to predict appropriate rent prices based on data scraped from NoBroker.

## Technologies Used

- **Android**: For developing the mobile application.
- **Firebase Firestore**: Backend as a Service (BaaS) for storing user data, property data, and images.
- **Machine Learning**: Model trained on web-scraped data from NoBroker to predict rental prices.

## Installation

### Prerequisites

- Android Studio
- Firebase Account

### Steps

1. **Clone the Repository**

   ```bash
   git clone https://github.com/your-username/your-repo-name.git](https://github.com/ShlokKamat/property_rentals.git
   ```

2. **Open in Android Studio**
   - Open Android Studio and select "Open an existing Android Studio project".
   - Navigate to the cloned repository folder and open it.

3. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app to your Firebase project and follow the instructions to download the `google-services.json` file.
   - Place the `google-services.json` file in the `app` directory of your project.

4. **Build and Run**
   - Sync the project with Gradle files.
   - Run the app on an emulator or a physical device.

## Usage

- **For Property Owners/Brokers**:
  - Register and log in to the app.
  - Navigate to the "List Property" section.
  - Enter property details and upload images.
  - Save the listing to make it visible to tenants.

- **For Tenants**:
  - Register and log in to the app.
  - Browse available properties using filters.
  - View detailed information and contact property incharge for properties of interest.
  - Use the rent prediction feature to get an estimate of fair rental prices.

## Machine Learning Model

The machine learning model is trained on data scraped from NoBroker. The data preprocessing, feature engineering, model training, and evaluation are done separately. The trained model is integrated into the app to provide real-time rent predictions.

## Contributing

Contributions are welcome! Please fork the repository and use a feature branch. Pull requests are warmly welcome.

## Acknowledgements

- **Prof. Vinaya Satyanarayana**: Examiner, BITS.
- **Pramod A M**: Supervisor.
- **Praveen Kolar Sriramappa**: Examiner.
- **SAP Labs India**: For providing the resources and support.
- **BITS**: For the academic opportunity.

## Contact

If you have any questions or suggestions, feel free to contact me at [shlokkamat@gmail.com]
