package com.wec.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换敏感词的符号
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    /***
     *初始化敏感词存放的文件sensitive_words.txt
     */
    @PostConstruct//注解：表示这是一个初始化方法（服务启动会自动初始化）
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive_words.txt");//写入读取文件在classes之下的路径,在黄色target之下
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));//把字节流先转成字符流在再转成缓冲流
        ){
            String keyWord;
            while ( (keyWord = reader.readLine()) != null){
                //添加到前缀树
                this.addKeyWord(keyWord);
            }
        }catch (IOException e){
            logger.error("加载敏感词失败"+e.getMessage());
        }

    }

    //将一个敏感词添加到前缀树中去
    private void addKeyWord(String keyWord){
        TrieNode tempNode = rootNode;
        for (int i = 0;i<keyWord.length();i++){
            char c = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null){
                //初始化字节的点
                subNode = new TrieNode();
                tempNode.addSubNote(c,subNode);
            }
            //让指针指向字节点，进入下一轮循环
            tempNode = subNode;
            //设置结束表示
            if (i == keyWord.length()-1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /***
     * 过滤敏感词
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();//变长字符串

        //算法
        while (position < text.length()){
            char c = text.charAt(position);
            //跳过符号（防止敏感词被符号隔开）
            if (isSymbol(c)){
                //若指针1指向根节点，将此符号计入结果，让指针二向下走一步
                if (tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间指针三都想下走一步
                position++;
                continue;
            }
            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                //以begin为开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++ begin;
                //指针1重新指向根节点
                tempNode = rootNode;
            }else if (tempNode.isKeyWordEnd){
                //发现了敏感词。将begin到position字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
                //指针1重新指向根节点
                tempNode = rootNode;
            }else {
                //检查下一个字符
                position++;
            }
        }
        //将最后一批字符计入到结果
        sb.append(text.substring(begin));
        return sb.toString();

    }
    //判断是否为符号
    private boolean isSymbol(Character c){
        //(c < 0x2E80 || c > 0x9FFF)东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
    /***
     * 前缀树
     */
    private class TrieNode{
        //关键词结束的标识
        private boolean isKeyWordEnd = false;

        //当前节点的子节点(key:下级节点字符；value：下级节点)
        private Map<Character,TrieNode> subNode = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点方法
        public void addSubNote(Character c,TrieNode node){
            subNode.put(c,node);
        }
        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNode.get(c);
        }
    }
}
