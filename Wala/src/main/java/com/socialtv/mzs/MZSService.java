package com.socialtv.mzs;

import com.android.volley.Request;
import com.socialtv.Response;
import com.socialtv.http.GsonRequest;
import com.socialtv.mzs.entity.BuyPropRefresh;
import com.socialtv.mzs.entity.BuyPropSuccess;
import com.socialtv.mzs.entity.GetAward;
import com.socialtv.mzs.entity.Lottery;
import com.socialtv.mzs.entity.LuckyDip;
import com.socialtv.mzs.entity.Marquee;
import com.socialtv.mzs.entity.RobTicket;
import com.socialtv.mzs.entity.VotesResult;
import com.socialtv.shop.entity.GetShipResponse;

import java.util.Map;

/**
 * Created by wlanjie on 14-9-2.
 * 梦之声所有的接口类
 */
public class MZSService {
    private final static String ROB_TICKET = "game/detail.json?id=%s";
    private final static String MOBILE_URI = "user/shipping_address.json";
    private final static String BUY_PROP_URI = "tool/order.json?tid=%s&pid=%s";
    private final static String BUY_PROP_CONFIRM = "order/confirm.json";
    private final static String ROB_TICKET_REFRESH = "tool/list.json?gid=%s";
    private final static String LOTTERY_URI = "game/drawlot.json?id=%s&tid=%s";
    private final static String MARQUEE_URI = "game/prize.json?id=%s";
    private final static String LUCKY_DIP_URI = "game/detail.json?id=%s";
    private final static String VOTES_URI = "game/vote.json?vid=%s&type=%s";
    private final static String GET_AWARD_URI = "game/vote_result.json?gid=%s";
    private final static String GET_AWARD_RESULT_URI = "game/drawlot.json?id=%s&vid=%s";

    /**
     * 抽奖URL
     * @param id
     * @return
     */
    public final Request<RobTicket> createRobTicketRequest(final String id) {
        GsonRequest<RobTicket> request = new GsonRequest<RobTicket>(Request.Method.GET, String.format(ROB_TICKET, id));
        request.setClazz(RobTicket.class);
        return request;
    }

    public final Request<GetShipResponse> createSubmitMobileRequest(final Map<String, String> bodys) {
        GsonRequest<GetShipResponse> request = new GsonRequest<GetShipResponse>(Request.Method.POST, MOBILE_URI);
        request.setClazz(GetShipResponse.class);
        request.setHeaders(bodys);
        return request;
    }

    public final Request<GetShipResponse> cretaeGetMobileRequest() {
        GsonRequest<GetShipResponse> request = new GsonRequest<GetShipResponse>(Request.Method.GET, MOBILE_URI);
        request.setClazz(GetShipResponse.class);
        return request;
    }

    public final Request<BuyPropSuccess> createBuyPropRequest(final String tid, final String pid) {
        GsonRequest<BuyPropSuccess> request = new GsonRequest<BuyPropSuccess>(Request.Method.GET, String.format(BUY_PROP_URI, tid, pid));
        request.setClazz(BuyPropSuccess.class);
        return request;
    }

    public final Request<BuyPropSuccess> createBuyPropConfirmRequest(final Map<String, String> bodys) {
        GsonRequest<BuyPropSuccess> request = new GsonRequest<BuyPropSuccess>(Request.Method.POST, BUY_PROP_CONFIRM);
        request.setHeaders(bodys);
        request.setClazz(BuyPropSuccess.class);
        return request;
    }

    public final Request<BuyPropRefresh> createRobTicketRefresh(final String id) {
        GsonRequest<BuyPropRefresh> request = new GsonRequest<BuyPropRefresh>(Request.Method.GET, String.format(ROB_TICKET_REFRESH, id));
        request.setClazz(BuyPropRefresh.class);
        return request;
    }

    public final Request<Lottery> createLotteryRequest(final String id, final String tid) {
        GsonRequest<Lottery> request = new GsonRequest<Lottery>(Request.Method.GET, String.format(LOTTERY_URI, id, tid));
        request.setClazz(Lottery.class);
        return request;
    }

    public final Request<Marquee> createMarqueeRequest(final String id) {
        GsonRequest<Marquee> request = new GsonRequest<Marquee>(Request.Method.GET, String.format(MARQUEE_URI, id));
        request.setClazz(Marquee.class);
        return request;
    }

    public final Request<LuckyDip> createLuckyDipRequest(final String id) {
        GsonRequest<LuckyDip> request = new GsonRequest<LuckyDip>(Request.Method.GET, String.format(LUCKY_DIP_URI, id));
        request.setClazz(LuckyDip.class);
        return request;
    }

    public final Request<VotesResult> createVotesRequest(final String vid, final String type) {
        GsonRequest<VotesResult> request = new GsonRequest<VotesResult>(Request.Method.GET, String.format(VOTES_URI, vid, type));
        request.setClazz(VotesResult.class);
        return request;
    }

    public final Request<GetAward> createGetAwardRequest(final String id ) {
        GsonRequest<GetAward> request = new GsonRequest<GetAward>(Request.Method.GET, String.format(GET_AWARD_URI, id));
        request.setClazz(GetAward.class);
        return request;
    }

    public final Request<Lottery> createGetAwardReusltRequest(final String id, final String vid) {
        GsonRequest<Lottery> request = new GsonRequest<Lottery>(Request.Method.GET, String.format(GET_AWARD_RESULT_URI, id, vid));
        request.setClazz(Lottery.class);
        return request;
    }
}
