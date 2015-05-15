/*
 Copyright 2015 BarD Software s.r.o

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.bardsoftware.papeeria.sufler.api.input;

public class SuflerInput {
    private Integer mySize;
    private String myQuery;

    public SuflerInput() {
    }

    public Integer getSize() {
        return mySize;
    }

    public void setSize(Integer size) {
        mySize = size;
    }

    public String getQuery() {
        return myQuery;
    }

    public void setQuery(String query) {
        myQuery = query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SuflerInput that = (SuflerInput) o;

        if (mySize != that.mySize) {
            return false;
        }
        if (!myQuery.equals(that.myQuery)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mySize;
        result = 31 * result + myQuery.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SearchInput{" +
                "size=" + mySize +
                ", query='" + myQuery + '\'' +
                '}';
    }
}
