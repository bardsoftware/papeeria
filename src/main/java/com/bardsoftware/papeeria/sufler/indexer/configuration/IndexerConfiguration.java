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

package com.bardsoftware.papeeria.sufler.indexer.configuration;

import java.util.ArrayList;
import java.util.List;

public class IndexerConfiguration {

    private String myIndexPath;
    private List<String> myDirectories = new ArrayList<>();

    public IndexerConfiguration(String indexPath, List<String> directories) {
        myIndexPath = indexPath;
        myDirectories = directories;
    }

    public String getIndexPath() {
        return myIndexPath;
    }

    public List<String> getDirectories() {
        return myDirectories;
    }
}
