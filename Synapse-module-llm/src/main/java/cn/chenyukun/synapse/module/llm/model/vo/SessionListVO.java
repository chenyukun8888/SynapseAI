package cn.chenyukun.synapse.module.llm.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

/**
 * 会话列表响应 VO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private List<SessionListItemVO> sessions;

    public SessionListVO() {
    }

    public SessionListVO(Long total, Integer page, Integer pageSize, Integer totalPages, List<SessionListItemVO> sessions) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.sessions = sessions;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<SessionListItemVO> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionListItemVO> sessions) {
        this.sessions = sessions;
    }

    @Override
    public String toString() {
        return "SessionListVO{" +
                "total=" + total +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                ", sessions=" + sessions +
                '}';
    }
}

