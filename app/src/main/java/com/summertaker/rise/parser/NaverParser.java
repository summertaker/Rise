package com.summertaker.rise.parser;

import android.util.Log;

import com.summertaker.rise.common.BaseParser;
import com.summertaker.rise.data.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class NaverParser extends BaseParser {

    public void parseFluctuation(String response, ArrayList<Item> items) {
        if (response == null || response.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(response);

        Elements tables = doc.getElementsByTag("table");
        for (Element table : tables) {
            String className = table.attr("class");
            if (!"type_2".equals(className)) {
                continue;
            }

            long id = 1;

            Elements trs = table.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");
                if (tds.size() != 12) {
                    continue;
                }

                String code;
                String name;
                int price;
                int pof;
                float rof;
                int vot;
                float per;
                float roe;
                String temp;

                Elements a = tds.get(1).getElementsByTag("a");
                String href = a.attr("href");
                code = getCodeFromUrl(href);
                name = a.text();

                temp = tds.get(2).text();
                temp = temp.replace(",", "");
                price = Integer.parseInt(temp);

                temp = tds.get(3).text();
                temp = temp.replace(",", "");
                pof = Integer.valueOf(temp);

                temp = tds.get(4).text();
                temp = temp.replace(",", "");
                temp = temp.replace("%", "");
                temp = temp.replace("％", "");
                rof = Float.valueOf(temp);

                //if (rateOfFluctuation > 0) {
                //    if (siteId.equals(Config.KEY_FLUCTUATION_RISE) && rof < rateOfFluctuation) {
                //            continue;
                //    }
                //    else if (siteId.equals(Config.KEY_FLUCTUATION_FALL) && rof > -rateOfFluctuation) {
                //        continue;
                //    }
                //}

                temp = tds.get(5).text();
                temp = temp.replace(",", "");
                vot = Integer.valueOf(temp);

                temp = tds.get(10).text();
                temp = temp.replace(",", "");
                temp = temp.replace("%", "");
                temp = temp.replace("％", "");
                temp = temp.replace("N/A", "0");
                per = Float.valueOf(temp);

                temp = tds.get(11).text();
                temp = temp.replace(",", "");
                temp = temp.replace("%", "");
                temp = temp.replace("％", "");
                temp = temp.replace("N/A", "0");
                roe = Float.valueOf(temp);

                //title = title.replaceAll("\\[.*\\]", "");

                //Log.e(TAG, "itemCd: " + code + ", " + name + ", " + price + ", " + vot);

                Item item = new Item();
                item.setId(id);
                item.setCode(code);
                item.setName(name);
                item.setPrice(price);
                item.setPof(pof);
                item.setRof(rof);
                item.setVot(vot);
                item.setPer(per);
                item.setRoe(roe);

                items.add(item);
                id++;
            }
        }
    }
}
