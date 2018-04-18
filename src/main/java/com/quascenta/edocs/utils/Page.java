package com.quascenta.edocs.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Software_Development on 12/10/2017.
 */
public class Page<E>  {
    private int pagesAvailable;
    private int totalRecordCount;
    private int previousPage;
    private int nextPage;
    private int lastPage;
    private int firstPage;

    private List<E> pageItems = new ArrayList<>();

    public void setPagesAvailable(int pagesAvailable) {
        this.pagesAvailable = pagesAvailable;
    }

    public void setPageItems(List<E> pageItems) {
        this.pageItems = pageItems;
    }

    public int getPagesAvailable() {
        return pagesAvailable;
    }

    public List<E> getPageItems() {
        return pageItems;
    }

    public int getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(int totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPreviousPage() {
        return previousPage;
    }

    public void setPreviousPage(int previousPage) {
        this.previousPage = previousPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }
}
