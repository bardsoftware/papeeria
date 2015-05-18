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

package com.bardsoftware.papeeria.sufler.api.output;

import java.util.ArrayList;
import java.util.List;

public class Source {
    private List<String> myCreators;
    private List<String> myTitles;
    private String myDescription;
    private List<String> myIdentifiers;
    private List<String> mySubjects;
    private List<String> myDates;
    private float myScore;

    public Source() {
        myCreators = new ArrayList<>();
        myTitles = new ArrayList<>();
        //myDescription = new ArrayList<>();
        myIdentifiers = new ArrayList<>();
        mySubjects = new ArrayList<>();
        myDates = new ArrayList<>();
    }

    public List<String> getCreators() {
        return myCreators;
    }

    public void setCreators(List<String> creators) {
        myCreators = creators;
    }

    public List<String> getTitles() {
        return myTitles;
    }

    public void setTitles(List<String> title) {
        myTitles = title;
    }

    public String getDescription() {
        return myDescription;
    }

    public void setDescription(String description) {
        myDescription = description;
    }

    public List<String> getIdentifiers() {
        return myIdentifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        myIdentifiers = identifiers;
    }

    public List<String> getSubjects() {
        return mySubjects;
    }

    public void setSubjects(List<String> subjects) {
        mySubjects = subjects;
    }

    public List<String> getDates() {
        return myDates;
    }

    public void setDates(List<String> date) {
        myDates = date;
    }

    public float getScore() {
        return myScore;
    }

    public void setScore(float score) {
        myScore = score;
    }
}
