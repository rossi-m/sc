package com.leyou.search.pojo;

public class SearchRequest {
    private String key; //搜索字段
    private Integer page; //当前页
    private static final int DEFAULT_SIZE =20; //每页大小，不从页面接受，而是固定大小
    private static final int DEFAULT_PAGE=1;// 默认页

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page==null){
            return DEFAULT_PAGE;
        }
        return Math.max(DEFAULT_PAGE,page); //获取DEFAULT_PAGE 和 page中的最大值
    }

    public void setPage(int page) {
        this.page = page;
    }
    public Integer getSize(){
        return DEFAULT_SIZE;
    }
}
