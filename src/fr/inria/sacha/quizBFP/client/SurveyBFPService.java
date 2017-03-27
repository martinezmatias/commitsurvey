/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package fr.inria.sacha.quizBFP.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.inria.sacha.logic.shared.BugFixPatternResult;
import fr.inria.sacha.logic.shared.BugFixQuizItem;
import fr.inria.sacha.logic.shared.BugFixQuizResult;

@RemoteServiceRelativePath("SurveyBFPService")
public interface SurveyBFPService extends RemoteService {
	
	
	public Integer start(String user, String dataset);
	
	public BugFixQuizItem getNextBugFixItem(String user);
	
	public void saveResult(BugFixQuizResult result);

	public List<String> getClassification(String user);
	
	public List<BugFixPatternResult> analyzeResults();
	
}
