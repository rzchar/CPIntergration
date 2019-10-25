package edu.nju.ics.pgs.ide.part;

import static java.lang.Math.*;

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import edu.nju.ics.pgs.ide.action.ValuePool;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;

public class ResultViewImpl extends BaseView<ResultView.ActionDelegate> implements ResultView {

  interface ResultViewImplUiBinder extends UiBinder<Widget, ResultViewImpl> {}

  @UiField FlowPanel mainPanel;

  @UiField FlowPanel countPanel;

  @UiField FlowPanel apiPanel;

  @UiField FlowPanel posPanel;

  //    @UiField
  //    ScrollPanel scrollPanel;

  private String filename;
  private int num_api;
  private int num_for_replace;
  private int num_para;
  private int num_pos;
  private int cur_start;

  private JSONArray api_arr;
  private JSONArray api_prob;
  private JSONArray select_call_arr;
  private JSONArray toReplace_arr;
  private JSONArray forReplace_arr;
  private JSONArray para_list_arr;
  private JSONArray replace_para_list_arr;
  private JSONArray pos;

  private String api;
  private String toReplace;
  private String[] paraClass;
  private JSONArray forReplace;
  private JSONArray para_list;
  private JSONArray replace_para_list;

  private ApiList select_api;
  private SelectItem select_call;
  private SelectItem[] select_para;

  private boolean python_caller_needed;
  private Boolean[] python_para_needed;

  @Inject
  public ResultViewImpl(PartStackUIResources resources, ResultViewImplUiBinder uiBinder) {
    //        super(resources);
    setContentWidget(uiBinder.createAndBindUi(this));
    //        scrollPanel.getElement().setTabIndex(0);
  }

  @Override
  public void setStart(int start) {
    this.cur_start = start;
  }

  @Override
  public void addItem(int index, int start, double prob) {
    ResultItem item = new ResultItem(index, start, prob, filename);
    posPanel.add(item);
  }

  @Override
  public void setInfo(String filename) {
    countPanel.getElement().setInnerHTML("File: " + filename);
  }

  @Override
  public void setApi(String api) {
    this.api = api;
    apiPanel.getElement().setInnerHTML(api);
  }

  @Override
  public void clearItem() {
    mainPanel.clear();
    apiPanel.clear();
    posPanel.clear();
    countPanel.getElement().setInnerHTML("");
  }

  /**
   * when selected api changed, new index should be set
   *
   * @param index
   */
  @Override
  public void setIndex(int index) {
    num_for_replace = 0;
    num_para = 0;
    mainPanel.clear();
    /**
     * Save the information and set default index i -----------------------------------------------
     */
    this.api = this.api_arr.get(index).isString().toString();
    this.api = this.api.substring(1, this.api.length() - 1);
    JSONString toReplace = toReplace_arr.get(index).isString();
    this.toReplace = toReplace.toString();
    this.toReplace = this.toReplace.substring(1, this.toReplace.length() - 1);
    this.forReplace = forReplace_arr.get(index).isArray();
    this.para_list = this.para_list_arr.get(index).isArray();
    this.replace_para_list = replace_para_list_arr.get(index).isArray();

    /** Set the caller selection ---------------------------------------------------------------- */
    int calls_size = this.forReplace.size();
    this.select_call = new SelectItem("Select the api caller: ");
    if (!(calls_size == 1 && this.forReplace.get(0).isString().toString().equals("\"\""))) {
      python_caller_needed = true;
      this.num_for_replace = calls_size;
      for (int i = 0; i < this.num_for_replace; i++) {
        String replaceCall = this.forReplace.get(i).isString().toString();
        replaceCall = replaceCall.substring(1, replaceCall.length() - 1);
        this.select_call.addItem(replaceCall);
      }
      this.select_call.addItem("decide later...");
      if (ValuePool.fileType.equals("Python")) {
        select_call.setSelectedIndex(select_call.getItemCount() - 1);
      }
      mainPanel.add(this.select_call);
    } else {
      if (ValuePool.fileType.equals("Java")) {
        this.select_call.addItem("new " + this.toReplace + "()");
        this.select_call.addItem("decide later...");
        mainPanel.add(this.select_call);
      } else if (ValuePool.fileType.equals("Python")) {
        python_caller_needed = false;
        // Do nothing
      }
    }

    /** Set the parameter selection ----------------------------------------------------- */
    int para_size = this.para_list.size();
    if (!(para_size == 1 && this.para_list.get(0).isString().toString().equals("\"\""))) {
      paraClass = new String[para_size];
      python_para_needed = new Boolean[para_size];
      this.num_para = para_size;
      this.select_para = new SelectItem[para_size];
      for (int i = 0; i < para_size; i++) {
        paraClass[i] = this.para_list.get(i).isString().toString();
        paraClass[i] = paraClass[i].substring(1, paraClass[i].length() - 1);
        this.select_para[i] = new SelectItem("Param " + (i + 1) + ": ");
        JSONArray select_para_list = replace_para_list.get(i).isArray();
        int paras_size = select_para_list.size();
        if (!(paras_size == 1 && select_para_list.get(0).isString().toString().equals("\"\""))) {
          python_para_needed[i] = true;
          for (int j = 0; j < paras_size; j++) {
            String replacePara = select_para_list.get(j).isString().toString();
            replacePara = replacePara.substring(1, replacePara.length() - 1);
            if (replacePara.charAt(0) == '\''
                && replacePara.charAt(replacePara.length() - 1) == '\'') {
              replacePara = "\"" + replacePara.substring(1, replacePara.length() - 1) + "\"";
            }
            this.select_para[i].addItem(replacePara);
          }
          this.select_para[i].addItem("decide later");
          mainPanel.add(this.select_para[i]);
        } else {
          if (ValuePool.fileType.equals("Java")) {
            this.select_para[i].addItem("new " + para_list.get(i).isString().toString() + "()");
            this.select_para[i].addItem("decide later");
            mainPanel.add(this.select_para[i]);
          } else if (ValuePool.fileType.equals("Python")) {
            python_para_needed[i] = false;
          }
        }
      }
    }

    FlowPanel posDescription = new FlowPanel();
    posDescription.getElement().setInnerHTML("<br><hr color=#999999><br>Positions to insert: ");
    mainPanel.add(posDescription);
  }

  @Override
  public void initialize(String response, String fileName) {

    num_for_replace = 0;
    num_para = 0;
    JSONObject responseJson = JSONParser.parseStrict(response).isObject();
    this.api_arr = responseJson.get("api").isArray();
    this.toReplace_arr = responseJson.get("toReplace").isArray();
    this.forReplace_arr = responseJson.get("forReplace").isArray();
    this.para_list_arr = responseJson.get("para_list").isArray();
    this.replace_para_list_arr = responseJson.get("replace_para_list").isArray();
    this.pos = responseJson.get("pos").isArray();
    this.num_pos = this.pos.size();
    this.filename = fileName;
    this.setInfo(fileName);

    /** Set Api -------------------------------------------------------------------- */
    int api_size = api_arr.size();
    if (!(api_size == 1 && api_arr.get(0).isString().toString().equals("\"\""))) {
      select_api = new ApiList("Select the api: ");
      this.num_api = api_size;
      for (int i = 0; i < this.num_api; i++) {
        String api = this.api_arr.get(i).isString().toString();
        api = api.substring(1, api.length() - 1);
        select_api.addItem(api);
      }
    }

    this.api_prob = responseJson.get("api_prob").isArray();
    SelectElement selectElement = SelectElement.as(select_api.getListBox().getElement());
    NodeList<OptionElement> options = selectElement.getOptions();

    double ratio = 1.0;
    for (int i = 0; i < this.num_api; i++) {
      double prob = this.api_prob.get(i).isNumber().doubleValue();
      if (i == 0) {
        prob = 0.9;
      } else if (i == 1) {
        prob = 0.5;
        ratio = 0.5 / pow(prob, 0.2);
      } else {
        prob = pow(prob, 0.2) * ratio;
      }
      prob = log10(prob + 1.0) * 3;
      String green = new Integer((new Double(prob * 100 + 50)).intValue()).toString();
      String rgb = "rgb(50," + green + ",50)";
      options.getItem(i).getStyle().setBackgroundColor(rgb);
      options.getItem(i).getStyle().setColor("#FFFFFF");
    }
    apiPanel.add(select_api);
    FlowPanel posDescription = new FlowPanel();
    posDescription.getElement().setInnerHTML("<br><hr color=#999999><br>");
    apiPanel.add(posDescription);

    this.setIndex(0);

    /**
     * Set the position selection
     * -------------------------------------------------------------------
     */
    int index = 1;
    ratio = 1.0;
    for (int i = 0; i < this.num_pos; i++) {
      JSONArray result_i = pos.get(i).isArray();
      Double start_row = result_i.get(0).isNumber().doubleValue();
      int start_i = start_row.intValue();
      if (i == 0) {
        this.cur_start = start_i;
      }
      Double prob_i = result_i.get(1).isNumber().doubleValue();
      if (i == 0) {
        prob_i = 0.9;
      } else if (i == 1) {
        prob_i = 0.5;
        ratio = 0.5 / pow(prob_i, 0.2);
      } else {
        prob_i = pow(prob_i, 0.2) * ratio;
      }
      prob_i = log10(prob_i + 1.0) * 3;
      if (start_i == 0) continue;

      this.addItem(index, start_i, prob_i);
      index++;
    }

    ValuePool.resultPresenter.updateCompare();
    ValuePool.comparePresenter.updateStackOverflow();
  }

  @Override
  public void updateCompare() {

    ValuePool.workspaceAgent.openPart(ValuePool.comparePresenter, PartStackType.INFORMATION);
    ValuePool.workspaceAgent.setActivePart(ValuePool.comparePresenter);
    ValuePool.comparePresenter.clearItem();
    ValuePool.comparePresenter.setInfo(this.api, this.cur_start);
    Integer addPos = 1;
    boolean added = false;
    for (int i = 0; i < ValuePool.oldCode.length; i++) {
      if (!added && i == this.cur_start) {
        String final_api = ValuePool.resultPresenter.genNewCode();
        if (i > 0) {
          for (int j = 0; j < ValuePool.oldCode[i - 1].length(); j++) {
            if (ValuePool.oldCode[i - 1].charAt(j) == ' ') {
              final_api = " " + final_api;
            } else if (ValuePool.oldCode[i - 1].charAt(j) == '\t') {
              final_api = "\t" + final_api;
            } else {
              break;
            }
          }
        }
        ValuePool.comparePresenter.addItem("add", final_api, addPos.toString());
        i--;
        added = true;
      } else {
        ValuePool.comparePresenter.addItem("normal", ValuePool.oldCode[i], addPos.toString());
      }
      addPos++;
    }

    ValuePool.comparePresenter.setHeight((ValuePool.oldCode.length + 10) * 18);
    ValuePool.comparePresenter.setScrollPosition(max(0, this.cur_start - 5) * 18);
  }

  @Override
  public String genNewCode() {

    String retStr = this.api;
    if (ValuePool.fileType.equals("Python") && python_caller_needed == false) {

    } else {
      String caller = "_";
      int index = select_call.getSelectedIndex();
      if (index < select_call.getItemCount() - 1) {
        caller = select_call.getItemText(index);
      }
      if (ValuePool.fileType.equals("Python") && caller.equals("_")) {

      } else {
        retStr = retStr.replaceFirst(this.toReplace, caller);
      }
    }

    //        ValuePool.notificationManager.notify(this.toReplace,
    // StatusNotification.Status.SUCCESS, StatusNotification.DisplayMode.EMERGE_MODE);

    for (int i = 0; i < num_para; i++) {
      String para = "_";
      if (ValuePool.fileType.equals("Python") && python_para_needed[i] == false) {

      } else {
        int index = select_para[i].getSelectedIndex();
        if (index < select_para[i].getItemCount() - 1) {
          para = select_para[i].getItemText(index);
        }
      }
      retStr = retStr.replaceFirst(paraClass[i], para);
    }
    retStr += ";";
    return retStr;
  }
}
