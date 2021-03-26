package com.jfloydconsult.matatu.model;

public class SurveyModel {

    public class ResponseToken{
        public String randomKey;
        public String token;
        public String expiresIn;

        public ResponseToken() {
        }

        public ResponseToken(String randomKey, String token, String expiresIn) {
            this.randomKey = randomKey;
            this.token = token;
            this.expiresIn = expiresIn;
        }

        public String getRandomKey() {
            return randomKey;
        }

        public void setRandomKey(String randomKey) {
            this.randomKey = randomKey;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(String expiresIn) {
            this.expiresIn = expiresIn;
        }
    }

    public class Result
    {
        public int code;
        public String mssage;
        public Data data;

        public Result(int code, String mssage, Data data) {
            this.code = code;
            this.mssage = mssage;
            this.data = data;
        }

        public Result() {
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMssage() {
            return mssage;
        }

        public void setMssage(String mssage) {
            this.mssage = mssage;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    public class Data
    {
        public String id;
        public String expiredate ;
        public String password;

        public Data(String id, String expiredate, String password) {
            this.id = id;
            this.expiredate = expiredate;
            this.password=password;
        }

        public Data() {
        }

        public String getId() {
            return id;
        }

        public String getPassword() {
            return password;
        }

        public String getExpiredate() {
            return expiredate;
        }
    }

    public static class Payment{
        public String account_no;
        public String reference;
        public String provider;
        public String msisdn;
        public String currency;
        public int amount;
        public String description;

        public Payment(String account_no, String reference, String provider, String msisdn, String currency, int amount, String description) {
            this.account_no = account_no;
            this.reference = reference;
            this.provider = provider;
            this.msisdn = msisdn;
            this.currency = currency;
            this.amount = amount;
            this.description = description;
        }

        public Payment() {
        }
    }

   public static class Logs{
        public String account_code;
        public String provider;
        public String msisdn;
        public String currency;
        public int amount;
        public int actual_amount;
        public int charge;
        public double developer_fee;
        public String transaction_id;
        public String tran_description;
        public String tran_type;
        public String user_id;
        public String period;
        public int count;

        public Logs() {
        }

        public Logs(String account_code, String provider, String msisdn, String currency, int amount, int actual_amount,
                    int charge, double developer_fee, String transaction_id, String tran_description, String tran_type,
                    String user_id, String period, int count) {
            this.account_code = account_code;
            this.provider = provider;
            this.msisdn = msisdn;
            this.currency = currency;
            this.amount = amount;
            this.actual_amount = actual_amount;
            this.charge = charge;
            this.developer_fee = developer_fee;
            this.transaction_id = transaction_id;
            this.tran_description = tran_description;
            this.tran_type = tran_type;
            this.user_id = user_id;
            this.period = period;
            this.count = count;
        }

   }
}
