package com.example.elasticsearch.dsto;

import com.example.elasticsearch.utils.Page;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.*;

/**
 * @author hell
 * @date 2018/4/16
 */
public class Condition {

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    /**
     * 用于es的搜索
     */
    protected SearchSourceBuilder sourceBuilder;

    protected Page page;

    public Condition() {
        oredCriteria = new ArrayList<>();
        sourceBuilder = new SearchSourceBuilder();
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void setOredCriteria(List<Criteria> oredCriteria) {
        this.oredCriteria = oredCriteria;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public Page getPage() {
        return page;
    }

    public SearchSourceBuilder getSourceBuilder() {
        return sourceBuilder;
    }

    public void setSourceBuilder(SearchSourceBuilder sourceBuilder) {
        this.sourceBuilder = sourceBuilder;
    }

    public void setPage(Page page) {
        sourceBuilder.from(page.getBegin());
        sourceBuilder.size(page.getLength());
        this.page = page;
    }

    /**
     * 创建查询条件（包括es的查询条件）
     * @return
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        sourceBuilder.query(criteria.getBoolBuilder());
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public static class Criteria{

        protected BoolQueryBuilder boolBuilder;

        protected List<Criterion> criteria;

        protected Criteria() {
            boolBuilder = QueryBuilders.boolQuery();
            criteria = new ArrayList<>();
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        public void setCriteria(List<Criterion> criteria) {
            this.criteria = criteria;
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public BoolQueryBuilder getBoolBuilder() {
            return boolBuilder;
        }

        public void setBoolBuilder(BoolQueryBuilder boolBuilder) {
            this.boolBuilder = boolBuilder;
        }

        public Criteria andEqualTo(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " = ",value));
            boolBuilder.must(QueryBuilders.matchQuery(field, value));
            return this;
        }

        public Criteria andNotEqualTo(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " <> ",value));
            boolBuilder.mustNot(QueryBuilders.matchQuery(field, value));
            return this;
        }

        public Criteria andGreaterThan(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " > ",value));
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);
            rangeQueryBuilder.gt(value);
            boolBuilder.must(rangeQueryBuilder);
            return this;
        }

        public Criteria andGreaterThanOrEqualTo(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " >= ",value));
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);
            rangeQueryBuilder.gte(value);
            boolBuilder.must(rangeQueryBuilder);
            return this;
        }

        public Criteria andLessThan(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " < ",value));
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);
            rangeQueryBuilder.lt(value);
            boolBuilder.must(rangeQueryBuilder);
            return this;
        }

        public Criteria andLessThanOrEqualTo(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " <= ",value));
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);
            rangeQueryBuilder.lte(value);
            boolBuilder.must(rangeQueryBuilder);
            return this;
        }

        public Criteria andIsNull(String field) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " is null "));
            boolBuilder.must(QueryBuilders.matchQuery(field, " "));
            return this;
        }

        public Criteria andIsNotNull(String field) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " is not null "));
            return this;
        }

        public Criteria andIn(String field,Iterable values) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " in ",values));
            String[] str = new String[2];
            str[0] = "book1";
            str[1] = "book2";
            boolBuilder.must(QueryBuilders.termsQuery(field,values));
            return this;
        }

        public Criteria andNotIn(String field,Iterable values) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " not in ",values,"andNotIn"));

            String[] str = new String[3];
            str[0] = "book1";
            str[0] = "book2";
            boolBuilder.mustNot(QueryBuilders.termsQuery(field, ""));
            return this;
        }

        public Criteria andBetween(String field,Object firstValue,Object secondValue) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " between " ,firstValue,secondValue));
            return this;
        }

        public Criteria andNotBetween(String field,Object firstValue,Object secondValue) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " not between " ,firstValue,secondValue));
            return this;
        }

        public Criteria andLike(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " like " ,value));
            return this;
        }

        public Criteria andNotLike(String field,Object value) {
            if (field == null) {
                throw new RuntimeException("Value for field cannot be null");
            }
            criteria.add(new Criterion(field + " not like " ,value));
            return this;
        }
    }

    public static class Criterion {
        private String methonName;
        /**
         * 查询的数据库字段
         */
        private String condition;
        /**
         * 查询的条件值
         */
        private Object value;
        /**
         * 第二个参数值（between语法）
         */
        private Object secondValue;
        /**
         * 没有值
         */
        private boolean noValue;
        /**
         * 单个值
         */
        private boolean singleValue;
        private boolean betweenValue;
        private boolean listValue;

        protected Criterion(String condition) {
            this.condition = condition;
            this.noValue = true;
        }

        protected Criterion(String condition,Object value) {
            this.condition = condition;
            this.value = value;
            if(value instanceof Collection<?>){
                this.listValue = true;
            }else{
                this.singleValue = true;
            }
        }

        protected Criterion(String condition,Object firstValue, Object secondValue) {
            this.condition = condition;
            this.value = firstValue;
            this.secondValue = secondValue;
            this.betweenValue = true;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public void setSecondValue(Object secondValue) {
            this.secondValue = secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public void setNoValue(boolean noValue) {
            this.noValue = noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public void setSingleValue(boolean singleValue) {
            this.singleValue = singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public void setBetweenValue(boolean betweenValue) {
            this.betweenValue = betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public void setListValue(boolean listValue) {
            this.listValue = listValue;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }
    }
}
