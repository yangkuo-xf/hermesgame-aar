package com.hermesgamesdk.entity;

import java.util.List;

public class WalletInfo {
    private String amount;
    private int useWallet;

    private List<InitData.Paytypes> payTypes;
    private List<Vouchers> vouchers;


    private String errTips="";

    public String getErrTips() {
        return errTips;
    }

    public void setErrTips(String errTips) {
        this.errTips = errTips;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setUseWallet(int useWallet) {
        this.useWallet = useWallet;
    }

    public int getUseWallet() {
        return useWallet;
    }

    public void setPayTypes(List<InitData.Paytypes> payTypes) {
        this.payTypes = payTypes;
    }

    public List<InitData.Paytypes> getPayTypes() {
        return payTypes;
    }

    public void setVouchers(List<Vouchers> vouchers) {
        this.vouchers = vouchers;
    }

    public List<Vouchers> getVouchers() {
        if (vouchers!=null&&vouchers.size()!=0){
            return vouchers;
        }else{
            return null;
        }

    }

    public class Vouchers {
        private String code;

        private String btime;
        private String etime;

        public String getBtimeUnix() {
            return btimeUnix;
        }

        public void setBtimeUnix(String btimeUnix) {
            this.btimeUnix = btimeUnix;
        }

        public String getEtimeUnix() {
            return etimeUnix;
        }

        public void setEtimeUnix(String etimeUnix) {
            this.etimeUnix = etimeUnix;
        }

        private String btimeUnix;
        private String etimeUnix;
        private String amount;

        public String getLimitDesc() {
            return limitDesc;
        }

        public void setLimitDesc(String limitDesc) {
            this.limitDesc = limitDesc;
        }

        private String limitDesc;
        private int isViable;

        public void setCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }


        public void setBtime(String btime) {
            this.btime = btime;
        }

        public String getBtime() {
            return btime;
        }

        public void setEtime(String etime) {
            this.etime = etime;
        }

        public String getEtime() {
            return etime;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAmount() {
            return amount;
        }

        public void setIsViable(int isViable) {
            this.isViable = isViable;
        }

        public int getIsViable() {
            return isViable;
        }

    }

}
