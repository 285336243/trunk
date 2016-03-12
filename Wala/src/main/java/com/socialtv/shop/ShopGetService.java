package com.socialtv.shop;

import com.android.volley.Request;
import com.socialtv.http.GsonRequest;
import com.socialtv.shop.entity.AdVert;
import com.socialtv.shop.entity.GetShipResponse;
import com.socialtv.shop.entity.Goods;
import com.socialtv.shop.entity.ShopDetaildResponse;


/**
 * person atrr url create
 */
public class ShopGetService {

    private static final String SHOP_LIST = "shop/list.json?page=%s&cnt=%s";
    private static final String ADVERT_BANNER ="shop/banner.json";
    private static final String SHOP_DETAILS ="shop/detail.json?id=%s";
    private static final String SHIPPING_URL ="user/shipping_address.json";

    public Request<Goods> createGoodsRequest(int page,int count) {
        GsonRequest<Goods> request = new GsonRequest<Goods>(Request.Method.GET, String.format(SHOP_LIST,page,count));
        request.setClazz(Goods.class);
        return request;
    }

   public Request<AdVert> createAdVertRequest( ) {
        GsonRequest<AdVert> request = new GsonRequest<AdVert>(Request.Method.GET, ADVERT_BANNER);
        request.setClazz(AdVert.class);
        return request;
    }

    public Request<ShopDetaildResponse> createShopDetailsRequest(final String id) {
        GsonRequest<ShopDetaildResponse> request = new GsonRequest<ShopDetaildResponse>(Request.Method.GET, String.format(SHOP_DETAILS,id));
        request.setClazz(ShopDetaildResponse.class);
        return request;

    }
     public Request<GetShipResponse>creatShippingRequest(){
        GsonRequest<GetShipResponse> request=new GsonRequest<GetShipResponse>(Request.Method.GET,SHIPPING_URL);
        request.setClazz(GetShipResponse.class);
        return  request;
    }
}
