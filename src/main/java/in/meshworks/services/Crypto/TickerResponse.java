package in.meshworks.services.Crypto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dhruv.suri on 04/12/17.
 */
public class TickerResponse {
    String success;
    String message;

    @SerializedName("result")
    private Result result;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result{
        @SerializedName("Bid")
        private String bid;

        @SerializedName("Ask")
        private String ask;

        @SerializedName("Last")
        private String last;

        public String getBid() {
            return bid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        public String getAsk() {
            return ask;
        }

        public void setAsk(String ask) {
            this.ask = ask;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "bid='" + bid + '\'' +
                    ", ask='" + ask + '\'' +
                    ", last='" + last + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TickerResponse{" +
                "success='" + success + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
