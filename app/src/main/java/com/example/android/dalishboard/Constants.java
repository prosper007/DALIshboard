package com.example.android.dalishboard;

    //Constants class stores bundle keys and result codes as constants
    public final class Constants {
        public static final int SUCCESS_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        public static final String PACKAGE_NAME =
                "com.example.android.dalishboard";

        // key for passing result receiver between MainActivity and FetchAddressIntentService
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";

        // key for passing a lis of persons between MainActivity and FetchAddressIntentService
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                ".LOCATION_DATA_EXTRA";
    }
