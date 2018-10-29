package com.demo.shirodemo.service.impl;

import com.demo.shirodemo.dao.*;
import com.demo.shirodemo.entity.*;
import com.demo.shirodemo.entity.table.*;
import com.demo.shirodemo.service.*;
import com.demo.shirodemo.tool.ListUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private FinanceService financeService;
    @Autowired
    private WeChatService weChatService;
    @Autowired
    private FinanceBakeUpRepository bakeUpRepository;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private OrderRepository orderRepository;
    private Result result = new Result(false);
    @Autowired
    private TechService techService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private ForOrdersRepository forOrdersRepository;
    @Autowired
    private TaskExecutor taskExecutor;
    private  boolean isFull = false;

    @Autowired
    private RedisForMerchantService redisForMerchant;

    @Override
    public Result save(OrdersEntity ordersEntity) {
        try {
            orderRepository.save(ordersEntity);
            if (ordersEntity.getType()==2){
                // done: 2018/9/24 发送短信通知..
                confirmOrder(ordersEntity.getId());
                result = messageService.send2Merchant(userService.findByPhone(ordersEntity.getOperation_phone()).getUsername(),ordersEntity.getOperation_phone());
                return result;
            }
            if (ordersEntity.getType()==1){
                // TODO: 2018/9/24 前台通知
            }
            // TODO: 2018/10/19 微信消息通知
            result.setSuccess(true);
            result.setData(ordersEntity);
            result.setMsg("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        } finally {
            return result;
        }
    }

    @Override
    public Result findAllByType(int type) {
        try {
            List<ResultForOrder> list = forOrdersRepository.findAllByType(type);
            parseOrder(list);
            result.setData((list));
            result.setSuccess(true);
            result.setMsg("查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        } finally {
            return result;
        }

    }


    @Override
    public Result findAllByTechPhone(String phone) {
        try {
            List<ResultForOrder> list = forOrdersRepository.findAllByTechAndType(1, phone);
            parseOrder(list);
            result.setData(list);
            result.setSuccess(true);
            result.setMsg("查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        } finally {
            return result;
        }

    }

    public void parseOrder(List<ResultForOrder> listForOrder){
        isFull =false;
        if (listForOrder.size()>10){
            List<List<ResultForOrder>> lists = ListUtils.subList(listForOrder,2);
            for (List<ResultForOrder> list:lists){
                taskExecutor.execute(new Thread(()->{
//                        //分两个线程 头尾
//                        taskExecutor.execute(new Thread(()->{
//                            for (int mid = list.size()/2-1;mid>=0;mid--){
//                                list.get(mid).setMerchandiselists(parseMerchandise(list.get(mid).getMerchandise()));
//                            }
//                        }));
//                        taskExecutor.execute(new Thread(()->{
//                            for (int mid = list.size()/2,size = list.size();mid<size;mid++){
//                                list.get(mid).setMerchandiselists(parseMerchandise(list.get(mid).getMerchandise()));
//                            }
//
//                        }));
                    list.forEach((i)->{
                        i.setMerchandiselists(parseMerchandise(i.getMerchandise()));
                    });

                }));
            }
            while(!isFull){
                isFull = true;
                for (List<ResultForOrder> list:lists){
                    for (ResultForOrder a:list){
                       if (a.getMerchandiselists()==null){
                           isFull = false;
                           break;
                       }
                       if (!isFull){
                           break;
                       }
                    }
                }
            }
           log.error("查询完成");
            return;
        }else{
            listForOrder.forEach((i)->{
                i.setMerchandiselists(parseMerchandise(i.getMerchandise()));
            });
        }
    }


    @Override
    public Result findAll() {
        result = new Result(false);
        try {
            List<ResultForOrder> listForOrder = forOrdersRepository.findAll();
            parseOrder(listForOrder);

            result.setData(listForOrder);
            result.setSuccess(true);
            result.setMsg("查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        } finally {
            return result;
        }

    }

    @Override
    public Result findAllByCustomerPhoneWithList(String phone,int i) {
        result =new Result(false);
        try {
            WeChatEntity weChatEntity = weChatService.findByPhone(phone);
            List<ResultForOrder> listForOrder = forOrdersRepository.findAllByCustomerPhone(phone);
            parseOrder(listForOrder);
            if (i == 1){
                result.setData(listForOrder);
            }else {
                result.setData(new ResultForStudentOrder(listForOrder,weChatEntity.getUsername(),weChatEntity.getStudent_id()));
            }
            result.setSuccess(true);
            result.setMsg("查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        } finally {
            return result;
        }

    }


    @Override
    public Result findAllByTypeAndCustPhone(int type, String phone,int i) {
        result =new Result(false);
        try {
            WeChatEntity weChatEntity = weChatService.findByPhone(phone);
            List<ResultForOrder> listForOrder = forOrdersRepository.findAllByTypeAndPhone(type,phone);
            parseOrder(listForOrder);
            if (i ==1 ){
                result.setData(listForOrder);
            }else {
                result.setData(new ResultForStudentOrder(listForOrder,weChatEntity.getUsername(),weChatEntity.getStudent_id()));
            }
            result.setSuccess(true);
            result.setMsg("查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        } finally {
            return result;
        }

    }

    /**
     * 将软件列表保存为String 存入数据库
     * String 固定格式 type,id+version*sun;
     *
     * @param str
     * @return
     */
    @Override
    @Async
    public List<MerchandiseList> parseMerchandise(String str) {
        String[] list = str.split(";");
        List<MerchandiseList> merchandiseLists = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {

            String[] split1 = list[i].split("\\.");
            String type = split1[0]; //类型
            String[] merchandise = split1[1].split(",");
            switch (type) {
                case "1":

                    MerchandiseList<SoftwareInfo> soft = new MerchandiseList<>();
                    List<SoftwareInfo> infos = new ArrayList<>();
                    soft.setCategory("软件");
                    //遍历merchandise
                    for (int j = 0; j < merchandise.length; j++) {
                        String softId = merchandise[j].split("\\+")[0];
                        String softVersion = merchandise[j].split("\\+")[1];
                        SoftwareInfo info = (SoftwareInfo) redisForMerchant.findSoftByIdAndVersion(Integer.parseInt(softId), softVersion).getData();
                        infos.add(info);
                    }
                    soft.setList(infos);
                    merchandiseLists.add(soft);
                    break;

                case "3":
                    log.info("数位屏解析...");
                    MerchandiseList<DigitalScreen> screens = new MerchandiseList<>();
                    List<DigitalScreen> screenList = new ArrayList<>();
                    screens.setList(screenList);
                    screens.setCategory("数位屏");
                    //遍历merchandise
                    for (int j = 0; j < merchandise.length; j++) {
                        System.out.println(merchandise[j]);
                        String id = merchandise[j].split("\\*")[0];
                        String sum = merchandise[j].split("\\*")[1];
                        DigitalScreen screen = (DigitalScreen) merchantService.findScreenById(Integer.parseInt(id)).getData();
                        screen.setSum(Integer.parseInt(sum));
                        screens.getList().add(screen);
                    }
                    merchandiseLists.add(screens);
                    break;
                case "2":
                    MerchandiseList<DigitalBoard> boards = new MerchandiseList<>();
                    boards.setCategory("数位板");
                    List<DigitalBoard> boards1 = new ArrayList<>();
                    boards.setList(boards1);
                    //遍历merchandise  有s  m 之分 2+s*2
                    for (int j = 0; j < merchandise.length; j++) {
                        String[] cate = merchandise[j].split("\\+");
                        String id = cate[0];
                        String version = cate[1].split("\\*")[0];
                        log.info("version:  " + version);
                        String sum = cate[1].split("\\*")[1];
                        DigitalBoard board = (DigitalBoard) merchantService.findBoardById(Integer.valueOf(id)).getData();
                        board.setSum(Integer.parseInt(sum));
                        board.setVersion(version);
                        boards.getList().add(board);
                    }
                    merchandiseLists.add(boards);
                    break;
            }
        }
        return merchandiseLists;
    }

    /**
     * 将String 还原为 javabean
     *
     * @param lists
     * @return
     */
    @Override
    public String merchandiseToString(List<MerchandiseList> lists) {
        String str = "";
        for (MerchandiseList list : lists) {
            switch (list.getCategory()) {
                case "软件":
                    //type . id+version,id+version;
                    str += "1.";
                    List<SoftwareInfo> list1 = list.getList();
                    for (int i = 0; i < list1.size(); i++) {
                        if (i != list1.size() - 1) {
                            str += list1.get(i).getId() + "+" + list1.get(i).getVersions().get(0) + ",";
                        } else {
                            str += list1.get(i).getId() + "+" + list1.get(i).getVersions().get(0);
                        }
                    }
                    str += ";";
                    break;
                case "数位板":
                    //type . id+s*sum,id+s*sum;
                    str += "2.";
                    List<DigitalBoard> list2 = list.getList();
                    for (int i = 0; i < list2.size(); i++) {
                        if (i != list2.size() - 1) {
                            str += list2.get(i).getId() + "+" + list2.get(i).getVersion() + "*" + list2.get(i).getSum() + ",";
                        } else {
                            str += list2.get(i).getId() + "+" + list2.get(i).getVersion() + "*" + list2.get(i).getSum();
                        }
                    }
                    str += ";";
                    break;
                case "数位屏":
                    str += "3.";//3.id*sum,id*sum;
                    List<DigitalScreen> list3 = list.getList();
                    for (int i = 0; i < list3.size(); i++) {
                        if (i != list3.size() - 1) {
                            str += list3.get(i).getId() + "*" + list3.get(i).getSum() + ",";
                        } else {
                            str += list3.get(i).getId() + "*" + list3.get(i).getSum();
                        }
                    }
                    str += ";";
                    break;
            }

        }
        return str;
    }

//    @Override
//    public Result findByTypeAndStatus(int type, int status) {
//        result = new Result(false);
//        try {
//            List<OrdersEntity> list = orderRepository.findByTypeAndStatus(type, status);
//            List<ResultForOrder> forOrders = new ArrayList<>();
//            for (OrdersEntity entity : list) {
//                ResultForOrder forOrder = new ResultForOrder(entity);
//                forOrder.setMerchandiselists(parseMerchandise(entity.getMerchandise()));
//                forOrders.add(forOrder);
//            }
//            result.setData(forOrders);
//            result.setMsg("成功");
//            result.setSuccess(true);
//        } catch (Exception e) {
//            result.setSuccess(false);
//        }
//        return result;
//    }

    @Override
    public Result findByTypeAndStatus(int type, int status,int i) {
        result = new Result(false);
        try {
            if (i == 1 ){
                List<ResultForOrder> forOrders = forOrdersRepository.findByTypeAndStatus(type, status);
                parseOrder(forOrders);
                result.setData(forOrders);
            }else{
                List<ResultForOrder> list = forOrdersRepository.findByTypeAndStatus(type, status);
                parseOrder(list);
                List<ResultForStudentOrder> forStudentOrder = new ArrayList<>(10);
                for (ResultForOrder forOrder : list) {
                    WeChatEntity weChatEntity = weChatService.findByPhone(forOrder.getCustomer_phone());
                    Boolean isExist = false;
                    for (ResultForStudentOrder studentOrder:forStudentOrder){
                        //如果存在添加到list  不存在添加
                        if (weChatEntity.getUsername().equals(studentOrder.getCustomerName())){
                            studentOrder.getList().add(forOrder);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist){
                        ResultForStudentOrder resultForStudentOrder = new ResultForStudentOrder(new ArrayList<>(5),weChatEntity.getUsername(),weChatEntity.getStudent_id());
                        resultForStudentOrder.getList().add(forOrder);
                        forStudentOrder.add(resultForStudentOrder);
                        log.error("添加成功");
                    }
                }
                result.setData(forStudentOrder);
            }
            result.setMsg("成功");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }


    public List<ResultForOrder> changeResult(List<OrdersEntity> list) {
        List<ResultForOrder> forOrders = new ArrayList<>();
        //if list.size()>10 进行多线程处理
        //改为视图
        long start ;
        long end ;
        long mid;
        for (OrdersEntity entity : list) {
            start = (System.currentTimeMillis());
            ResultForOrder forOrder = forOrdersRepository.getOne(entity.getId());
            mid = (System.currentTimeMillis());
            log.error("逻辑判断时间"+(mid-start));
            forOrder.setMerchandiselists(parseMerchandise(entity.getMerchandise()));
            end  = (System.currentTimeMillis());
            log.error("解析时间"+(end-mid));
            log.error("运行时间"+(end-start));
            log.error("----------");
            forOrders.add(forOrder);
        }
        //如果size>5


        return forOrders;
    }

    @Override
    @Transactional
    public Result updateStatusById(int id, int status,String abandonReason) {
        try {
            OrdersEntity ordersEntity = orderRepository.findById(id);
            if (status == 2&&ordersEntity.getType()==1){
                //删除Tech for order　的数据
                techService.delOneRecord(ordersEntity);
            }
            if (status == 2&&ordersEntity.getType()==2){
                //删除Tech for order　的数据
                techService.delOneRecord(ordersEntity);
            }

            if (((status==1||status == -1||status ==-2 || status - ordersEntity.getStatus() <= 2))||(status == 2&&ordersEntity.getType()==2)) {
                ordersEntity.setStatus(status);
                ordersEntity.setRemark(abandonReason);
                orderRepository.save(ordersEntity);
                result.setSuccess(true);
                result.setData(ordersEntity);
                result.setMsg("成功");
                return result;
            }
            else   result.setMsg("违规操作status 不能超过两个状态 " + ordersEntity.getStatus() + " to status: " + status);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;

    }


    @Override
    public Result findOwnsOrders() {
        result = new Result(false);
        try {
            Subject subject = SecurityUtils.getSubject();
            UserEntity userEntity = (UserEntity) subject.getPrincipal();
            if (subject.hasRole("customer")) {
                //将string 转为 list
                List<ResultForOrder> forOrders = forOrdersRepository.findAllByCustomerPhone(userEntity.getPhone());
                parseOrder(forOrders);
                result.setData(forOrders);
                result.setSuccess(true);
                result.setMsg("成功");
                return result;
            } else if (subject.hasRole("tech")) {
                return findAllByTechPhone(userEntity.getPhone());
            } else {
                result.setSuccess(false);
                result.setMsg("权限不足");
            }
            return result;
        } catch (NullPointerException e) {
            result.setMsg("请登录后再操作");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("未知异常");
            return result;
        }
    }


    @Override
    @Transactional
    public Result abandonOrder(int id,String abandonReason) {
        result = new Result(false);
        log.error("准备修改中...");
        Subject subject = SecurityUtils.getSubject();
        UserEntity userEntity = (UserEntity) subject.getPrincipal();
        OrdersEntity ordersEntity = orderRepository.findById(id);
        if (userEntity.getPhone().equals(ordersEntity.getCustomer_phone())) {
            log.error("修改中...");
            return updateStatusById(id, -1,abandonReason);
        } else if (userEntity.getPhone().equals(ordersEntity.getOperation_phone())){
            //删除一条数据
            log.error("准备更换中...");
            abandonReason =ordersEntity.getRemark()+"   订单来至于 tech:"+userService.findByPhone(ordersEntity.getOperation_phone()).getUsername()+"  "+abandonReason;
            result = updateStatusById(id, 1,abandonReason);
            // done: 2018/9/21 重新分配订单  自动分配
            //先将状态改为0 再分配 分配完改为1
            TechForOrders tech = (TechForOrders) techService.getOne(userEntity.getPhone()).getData();
            tech.setStatus(0);
            techService.save(tech);
            result = distributionOrder(id);
            tech.setStatus(1);
            techService.save(tech);
            //短信通知
        }
        return result;
    }

    @Transactional
    @Override
    public Result distributionOrder(int id) {
        //根据orderId 分配订单
        result = new Result(false);
        try {
            OrdersEntity ordersEntity =  orderRepository.findById(id);
            if (ordersEntity==null){
                result.setMsg("不存在该笔订单");
                result.setSuccess(false);
                return result;
            }
            if (ordersEntity.getStatus()==1&&ordersEntity.getType()==1){
                result = techService.getOneForOrder(ordersEntity);
                if (result.getData()==null){
                     result.setMsg("当前暂无技术人员");
                     result.setSuccess(false);
                     return result;
                }
                TechForOrders techForOrders = (TechForOrders)result.getData();
                //                //修改订单信息   短信通知 技术人员
                ordersEntity.setStatus(2);
                ordersEntity.setOperation_phone(techForOrders.getPhone());

                //技术人员接单信息
                techForOrders.setAmount(techForOrders.getAmount()+1);
                String product =  ordersEntity.getMerchandise().split("\\d\\.")[1];
                techForOrders.setProduct(techForOrders.getProduct()+String.valueOf(techForOrders.getAmount()+"."+product));
                if (techForOrders.getAmount()==99){
                    techForOrders.setStatus(0);
                }
                log.info("  techService.save(techForOrders)   "+ techService.save(techForOrders).getProduct());

                //发送短信通知
                result = messageService.send2Tech(userService.findByPhone(techForOrders.getPhone()).getUsername(),techForOrders.getPhone());
                result.setMsg("分配成功 技术人员"+userService.findByPhone(techForOrders.getPhone()).getUsername());
                result.setData(ordersEntity);
            }
            else if(ordersEntity.getStatus()==1&&ordersEntity.getType()==2){
                result.setMsg("该订单属于小程序商家");
                result.setData(null);
                result.setSuccess(false);


            }else {
                result.setMsg("该订单当前状态不可分配");
                result.setSuccess(false);
                result.setData(null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Transactional
    @Override
    public Result distributionOrderByName(int id, String name) {
        //根据orderId 分配订单
        result = new Result(false);
        try {
            OrdersEntity ordersEntity =  orderRepository.findById(id);
            if (ordersEntity==null){
                result.setMsg("不存在该笔订单");
                result.setSuccess(false);
                return result;
            }
            if (ordersEntity.getStatus()==1&&ordersEntity.getType()==1){
                result = techService.getByName(name);
                if (!result.getSuccess()){
                    return result;
                }
                TechForOrders techForOrders = (TechForOrders)result.getData();
                //                //修改订单信息   短信通知 技术人员
                ordersEntity.setStatus(2);
                ordersEntity.setOperation_phone(techForOrders.getPhone());
                log.error(techForOrders.toString());
                //技术人员接单信息
                techForOrders.setAmount(techForOrders.getAmount()+1);
                String product =  ordersEntity.getMerchandise().split("\\d\\.")[1];
                techForOrders.setProduct(techForOrders.getProduct()+String.valueOf(techForOrders.getAmount()+"."+product));
                if (techForOrders.getAmount()==99){
                    techForOrders.setStatus(0);
                }
                log.info("  techService.save(techForOrders)   "+ techService.save(techForOrders).getProduct());

                //发送短信通知
                result = messageService.send2Tech(userService.findByPhone(techForOrders.getPhone()).getUsername(),techForOrders.getPhone());
                result.setMsg("分配成功 技术人员"+userService.findByPhone(techForOrders.getPhone()).getUsername());
                result.setData(ordersEntity);
            }
            else if(ordersEntity.getStatus()==1&&ordersEntity.getType()==2){
                result.setMsg("该订单属于小程序商家");
                result.setData(null);
                result.setSuccess(false);
            }else {
                result.setMsg("该订单当前状态不可分配");
                result.setSuccess(false);
                result.setData(null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Transactional
    @Override
    public Result confirmOrder(int id) {
        result = new Result(false);
        try{
            String phone = userRoleService.getListByRoleId(6).get(0).getPhone();
            OrdersEntity ordersEntity = orderRepository.findById(id);
            if (ordersEntity.getType()==2){
                if (ordersEntity.getStatus()==1){
                    ordersEntity.setOperation_phone(phone);
                    ordersEntity.setStatus(2);
                    orderRepository.save(ordersEntity);
                    result.setMsg("已确认");
                    result.setSuccess(true);
                    result.setData(ordersEntity);
                }
                else {
                    result.setMsg("该订单状态不可修改");
                    result.setSuccess(false);
                }
            }else {
                result.setMsg("该订单不属于小程序");
                result.setSuccess(false);
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setMsg("确认订单失败");
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public Result techCompleteOrder(int id) {
        result = new Result(false);
        try{
            OrdersEntity ordersEntity = orderRepository.findById(id);
            if (ordersEntity.getType()==1){
                if (ordersEntity.getStatus()==2){
                    ordersEntity.setStatus(3);
                    orderRepository.save(ordersEntity);
                    techService.delOneRecord(ordersEntity);
                    result = messageService.send2Customer(userService.findByPhone(ordersEntity.getCustomer_phone()).getUsername(),ordersEntity.getOperation_phone(),ordersEntity.getCustomer_phone());
                    result.setData(ordersEntity);
                }
                else {
                    result.setMsg("该订单状态不可修改");
                    result.setSuccess(false);
                }
            }else {
                result.setMsg("该订单不属于工作室");
                result.setSuccess(false);
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setMsg("确认订单失败");
            result.setSuccess(false);
        }
        return result;
    }

    @Transactional
    @Override
    public Result sendMessageToCustomer(int id) {
        result = new Result(false);
        try {
            OrdersEntity ordersEntity = orderRepository.findById(id);
            if (ordersEntity==null){
                result.setMsg("不存在该笔订单");
                return result;
            }else if (ordersEntity.getType()!=2){
                result.setMsg("您暂时无法修改软件订单，或者联系管理员修改订单类型");
                return result;
            }else if (ordersEntity.getStatus()!=2){
                result.setMsg("该状态暂时无法通知客户");
            }else {
                ordersEntity.setStatus(3);
                orderRepository.save(ordersEntity);

                //todo发送短信
                result =messageService.merchantSend2Customer(userService.findByPhone(ordersEntity.getCustomer_phone()).getUsername(),ordersEntity.getOperation_phone(),ordersEntity.getCustomer_phone());
                result.setData(ordersEntity);

            }
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
        }

        return result;
    }
    @Transactional
    @Override
    public Result completeOrder(int id) {
        result = new Result(false);
        try {
            OrdersEntity ordersEntity = orderRepository.findById(id);
            if (ordersEntity==null){
                result.setMsg("不存在该笔订单");
                return result;
            }else if (ordersEntity.getStatus()!=3){
                result.setMsg("该状态暂时无法完成交易");
            }else {
                ordersEntity.setStatus(4);
                ordersEntity.setDeal_time(new Date(System.currentTimeMillis()));
                orderRepository.save(ordersEntity);
                String describe_info;
                if (ordersEntity.getType()==1){
                    //软件  只需添加条记录
                    describe_info = "完成软件订单id: "+ordersEntity.getId()+" 时添加";
                    financeService.save(new FinanceEntity(describe_info,1,ordersEntity.getMoney(),userService.findByPhone(ordersEntity.getOperation_phone()).getUsername(),new Date(System.currentTimeMillis())));

                }
                else if (ordersEntity.getType()==2){
                    //添加2条记录  有一条提成记录type = 3
                    describe_info = "完成第三方订单id: "+ordersEntity.getId()+" 时的提成";
                    financeService.save(new FinanceEntity(describe_info,3,financeService.getProfit(ordersEntity),userService.findByPhone(ordersEntity.getOperation_phone()).getUsername(),new Date(System.currentTimeMillis())));
                    describe_info = "完成第三方订单id: "+ordersEntity.getId()+" 时添加";
                    financeService.save(new FinanceEntity(describe_info,2,ordersEntity.getMoney(),userService.findByPhone(ordersEntity.getOperation_phone()).getUsername(),new Date(System.currentTimeMillis())));
                    // TODO: 2018/9/25 产品销售次数

                    List<MerchandiseList> softlists = parseMerchandise(ordersEntity.getMerchandise());
                    List<DigitalBoard> boardLists ;
                    List<DigitalScreen> screenLists;
                    for (MerchandiseList merchandiseList:softlists){
                        switch (merchandiseList.getCategory()){
                            case "数位板":
                                boardLists = (List<DigitalBoard>)merchandiseList.getList();
                                for (DigitalBoard digitalBoard:boardLists){
                                    ReentrantLock lock = new ReentrantLock();
                                    lock.lock();
                                    try {
                                        digitalBoard.setSum(digitalBoard.getSum()+1);
                                    }finally {
                                        lock.unlock();
                                    }
                                }
                                break;
                            case "数位屏":
                                screenLists = (List<DigitalScreen>)merchandiseList.getList();
                                for (DigitalScreen screen:screenLists){
                                    ReentrantLock lock = new ReentrantLock();
                                    lock.lock();
                                    try {
                                        screen.setSum(screen.getSum()+1);
                                    }finally {
                                        lock.unlock();
                                    }
                                }
                                break;
                        }

                }
}
                result.setSuccess(true);
                result.setMsg("交易成功");
                result.setData(ordersEntity);

            }
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }
    @Transactional
    @Override
    public Result inDoubtOrder(int id) {
        result = new Result(false);
        try {
            OrdersEntity ordersEntity = orderRepository.findById(id);
            if (ordersEntity==null){
                result.setMsg("不存在该笔订单");
                return result;
            }
            else {
                if (ordersEntity.getStatus()==4){
                    //删除财务记录
                    // TODO: 2018/9/24 删除财务记录 利用模糊查询来删除 再添加到备份表中
                    List<FinanceEntity> financeEntities = financeService.findAllByDescribe_infoContaining("id: "+ordersEntity.getId()+" 时");
                    financeEntities.forEach((financeEntity)->
                        {
                            if (financeEntity.getRemarks()==null){
                                financeEntity.setRemarks("id :"+financeEntity.getId());
                            }
                            bakeUpRepository.save(new FinanceBakeUpEntity(financeEntity));
                            financeEntity.setRemarks(financeEntity.getRemarks()+"用户对订单存在疑问 可查询备份表"+" money "+financeEntity.getMoney()+" ---> 0");
                            financeEntity.setMoney(0);
                            financeService.save(financeEntity);
                        });

                }
                ordersEntity.setStatus(-2);
                orderRepository.save(ordersEntity);
                result.setSuccess(true);
                result.setMsg("提交成功");
                result.setData(ordersEntity);

            }
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }
    @Transactional
    @Override
    public Result distributionDoubtOrder(int id) {
        OrdersEntity ordersEntity = orderRepository.findById(id);
            if (ordersEntity.getStatus()==-2){
                return updateStatusById(id,1,null);
            }
            return new Result(false,"该订单不存在问题，暂不可使用本方法");
    }
}