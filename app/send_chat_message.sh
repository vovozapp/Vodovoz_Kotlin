curl -X POST -H "Authorization: key=AAAAVDRk2dI:APA91bFhJbuY4JSilZY911uqEawOmU-Z-pl0gi9pjd9DPQeRo1dywL2Tpjw0m7MWZtglJvTjLCRNtndDc7K7v0CTDqgi_xH84I1ZvcSfCHDNr0EY1gdxMAp63BMIcYr8GaO5GYHfRqtP" -H "Content-Type: application/json" -d '{
    "to":"eeqlYFZCRlulXr9TRcG8xa:APA91bHM00Qsf9eGfSeRTE5HyHSQyPHa_BsUjOQZyQaTafdZH8Fda6btPWYu_Jy4qSdDrjtV2-UniE4pjlPlDKNgbzw3X2HrHrqbi4YYyz4-JS7ujzuKvBOtuxC_vQB4ayS4QDhl1zEG",
    "data": {
        "Secreen":"BRANDY",
        "ID":"new",
        "SUBSECTIONS":"new",
        "NumberZakaz":"new",
        "NAME_RAZDEL":"new"
     },
     "notification": {
        "image":"https://vodovoz.ru/upload/iblock/98a/98a42013dfc7d167aafd6370ed2e652b.jpg",
        "title":"Водовоз - Вода 19л",
        "body":"Водовоз - Вода 19л"
      }

}' https://fcm.googleapis.com/fcm/send -v -i