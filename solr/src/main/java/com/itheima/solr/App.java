package com.itheima.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class App {

    @Test
    public void testCreateIndexBySolrInputDocument() throws IOException, SolrServerException {
        String baseURL = "http://localhost:8080/solr";
        HttpSolrServer httpSolrServer = new HttpSolrServer(baseURL);
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id","8");
        doc.addField("title","sanae sanae");
        doc.addField("content","lovelove");
        httpSolrServer.add(doc);
        httpSolrServer.commit();
    }

    @Test
    public void testDeleteById() throws IOException, SolrServerException {
        String baseURL = "http://localhost:8080/solr";
        HttpSolrServer httpSolrServer = new HttpSolrServer(baseURL);
        httpSolrServer.deleteById("9");
        httpSolrServer.commit();
    }

    @Test
    public void findAll() throws SolrServerException {
        String baseURL = "http://localhost:8080/solr";
        SolrServer solrServer = new HttpSolrServer(baseURL);
        SolrQuery query = new SolrQuery();
        query.set("q","id:8");
        QueryResponse response = solrServer.query(query);
        SolrDocumentList list = response.getResults();
        for (SolrDocument solrDocument : list){
            System.out.println("id:"+solrDocument.get("id"));
            System.out.println("title:"+solrDocument.get("title"));
            System.out.println("content:"+solrDocument.get("content"));
        }
    }


    @Test
    public void searchIndex() throws SolrServerException {
        //创建solr服务对象
        String url = "http://localhost:8080/solr/products";
        SolrServer solrServer = new HttpSolrServer(url);
        //创建查询对象
        SolrQuery solrQuery = new SolrQuery();
        //设置查询语句
        solrQuery.setQuery("*:*");
        QueryResponse response = solrServer.query(solrQuery);
        //获取结果集
        SolrDocumentList solrDocumentList = response.getResults();
        //输出
        System.out.println("命中文档总数:"+solrDocumentList.getNumFound());
        for (SolrDocument solrDocument : solrDocumentList){
            System.out.println(solrDocument.get("id"));
            System.out.println(solrDocument.get("product_name"));
            System.out.println(solrDocument.get("product_price"));
        }
    }

    @Test
    public void searchCostom() throws Exception {
        //创建solr服务对象
        String url = "http://localhost:8080/solr/products";
        SolrServer solrServer = new HttpSolrServer(url);
        //创建查询对象
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("魔术");
        //设置查询过滤条件
        solrQuery.addFilterQuery("product_catalog_name:环保餐具");
        solrQuery.addFilterQuery("product_price:[1 TO 20]");
        //设置排序,第一个参数指定对哪个域进行排序,第二个设置升序,降序
        solrQuery.setSort("product_price", SolrQuery.ORDER.asc);
        //分页
        solrQuery.setStart(1);
        solrQuery.setRows(6);
        //设置高亮
        solrQuery.setHighlight(true);
        //指定设置高亮字段
        solrQuery.addHighlightField("product_name");
        //指定高亮显示前缀
        solrQuery.setHighlightSimplePre("<font color='red'>");
        //指定高亮显示后缀
        solrQuery.setHighlightSimplePost("</font>");
        //设置默认查询字段,默认查询通常与主查询结合使用
        solrQuery.set("df","product_keywords");

        QueryResponse response = solrServer.query(solrQuery);
        SolrDocumentList solrDocumentList = response.getResults();
        long numFound = solrDocumentList.getNumFound();
        System.out.println("命中总记录数:"+numFound);
        for (SolrDocument sdoc : solrDocumentList){
            String id = (String) sdoc.get("id");
            System.out.println("文档ID:"+id);
            String product_name = (String) sdoc.get("product_name");

            //获取高亮
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            //第一个map的key是id
            Map<String, List<String>> map = highlighting.get(id);
            //第二个map的key是域名
            List<String> list = map.get("product_name");

            if (list!=null && list.size()>0){
                product_name = list.get(0);
            }
            System.out.println("商品名称"+product_name);
            Float product_price = (Float) sdoc.get("product_price");
            System.out.println("商品价格"+product_price);
            String product_description = (String) sdoc.get("product_description");
            System.out.println("商品描述"+product_description);
            String product_picture = (String) sdoc.get("product_picture");
            System.out.println("商品图片"+product_picture);
            String product_catalog_name = (String) sdoc.get("product_catalog_name");
            System.out.println("商品名称"+product_catalog_name);
        }
    }

    public void fff(){
        System.out.println("test");
    }
}
