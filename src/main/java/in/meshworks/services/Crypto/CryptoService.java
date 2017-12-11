package in.meshworks.services.Crypto;

import com.google.common.collect.EvictingQueue;
import in.meshworks.utils.AzazteUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.*;

/**
 * Created by dhruv.suri on 04/12/17.
 */
public class CryptoService extends Thread {
    private Double startingAmount = 100.00;
    private Double tether = 0.00;
    private String currencyName;
    private String currencyMarketName;
    private Double currencyUnits = 0.00;
    private Double tradedStartAmount = 0.00;
    private DateTime priceLastAdded;

    private int upSell = 5;
    private int downSell = 1;
    private int minDifference = 1;

    private int sleepTime = 1000;

    private EvictingQueue<Double> queue = EvictingQueue.create(5);

    public CryptoService(String currencyName, String marketName) {
        this.currencyName = currencyName;
        this.currencyMarketName = marketName;
        tether = startingAmount;
        priceLastAdded = new DateTime();
        Double startingPrice = getUnitPriceInDollars();
        queue.add(startingPrice);
        queue.add(startingPrice);
        queue.add(startingPrice);
        queue.add(startingPrice);
        queue.add(startingPrice);
    }

    public static void main(String args[]) {
        CryptoService mco = new CryptoService("mco", "btc-mco");
        mco.start();

        CryptoService iop = new CryptoService("iop", "btc-iop");
        iop.start();

        CryptoService vox = new CryptoService("vox", "btc-vox");
        vox.start();

        CryptoService sys = new CryptoService("sys", "btc-sys");
        sys.start();

        CryptoService ardr = new CryptoService("ardr", "btc-ardr");
        ardr.start();

        CryptoService neo = new CryptoService("neo", "btc-neo");
        neo.start();

        CryptoService sc = new CryptoService("mona", "btc-mona");
        sc.start();

        List<CryptoService> list = new ArrayList<>();
        list.add(mco);
        list.add(iop);
        list.add(vox);
        list.add(sys);
        list.add(ardr);
        list.add(neo);
        list.add(sc);

        int i = 0;
        while (i < 1000) {
            int sum = 0;
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*****Wallet balances*****");
            for (CryptoService cryptoService : list) {
                System.out.println(cryptoService.currencyName + " : $" + cryptoService.getWalletBalance());
                sum = (int) (sum + cryptoService.getWalletBalance());
            }

            System.out.println("*******Total : " + sum + "***********");

        }
    }


    @Override
    public void run() {
        int i = 0;
        while (i < 1000) {
            try {
                Thread.sleep(this.sleepTime);
                Double currentTradeValue = getValueInDollars();
                if (currentTradeValue - tradedStartAmount > upSell || tradedStartAmount - currentTradeValue > downSell) {
                    sell();
                }

                if (shouldBuy()) {
                    buy();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Double getWalletBalance() {
        return tether + currencyUnits * getUnitPriceInDollars();
    }

    public void buy() {
        Double priceInDollars = getUnitPriceInDollars();
        if (tether <= 0) {
            return;
        }
        currencyUnits = tether / priceInDollars;
        tradedStartAmount = tether;
        tether = 0.00;
        System.out.println("Currency Name : " + currencyName + " --- Buy completed : purchased for : " + tradedStartAmount);
    }

    public void sell() {
        if (currencyUnits <= 0) {
            System.out.println("Insufficient currencies to sell");
            tether = tether + 0;
            return;
        }

//        TODO: API to sell currency
        tether = tether + getValueInDollars();
        System.out.println("Currency Name : " + currencyName + " ---- Sale completed - Trade output : " + (tether - tradedStartAmount));
        tradedStartAmount = 0.00;
        currencyUnits = 0.00;


    }

    private Double getUnitPriceInDollars() {
        TickerResponse ticker = getTicker(currencyMarketName);
        if (currencyMarketName.startsWith("btc")) {
            String last = ticker.getResult().getLast();
            TickerResponse btcTicket = getTicker("usdt-btc");
            Double btcValueInDollar = Double.valueOf(btcTicket.getResult().getLast());
            return Double.valueOf(last) * btcValueInDollar;
        } else {
            return Double.valueOf(ticker.getResult().getLast());
        }
    }

    private Double getValueInDollars() {
        return getUnitPriceInDollars() * currencyUnits;
    }


    private TickerResponse getTicker(String currencyMarketName) {
        String primaryUrl = "http://api.flaircraft.co:8080/proxy?url=https://bittrex.com/api/v1.1/public/getticker?market=";
        String secondaryUrl = "https://bittrex.com/api/v1.1/public/getticker?market=";
        TickerResponse response = null;
        try {
            response = getTickerTrial(primaryUrl, currencyMarketName);
        } catch (Exception e) {
            System.out.println("Primary Ticker Failed");
        }

        if (response == null) {
            try {
                response = getTickerTrial(secondaryUrl, currencyMarketName);
            } catch (Exception e) {
                System.out.println("Secondry Ticker Failed");
            }
        }
        return response;
    }

    private TickerResponse getTickerTrial(String tickerUrl, String currencyMarketName) throws IOException {
        String url = tickerUrl + currencyMarketName + "&type=0";
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().url(url).build();
        Response execute = client.newCall(req).execute();
        String tickerResponseString = execute.body().string();
        TickerResponse tickerResponse = null;
        try{
            tickerResponse = AzazteUtils.fromJson(tickerResponseString, TickerResponse.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        return tickerResponse;
    }

    private Boolean shouldBuy() {
        int end = new DateTime().getMinuteOfDay();
        int start = priceLastAdded.getMinuteOfDay();
        if (end - start >= minDifference) {
            queue.add(getUnitPriceInDollars());
            priceLastAdded = new DateTime();
        }

        double diff = 0;
        double last = 0;
        for (Double aDouble : queue) {
            if (last == 0) {
                last = aDouble;
                continue;
            }
            diff = diff + aDouble - last;
        }
        return diff > 0;
    }
}

