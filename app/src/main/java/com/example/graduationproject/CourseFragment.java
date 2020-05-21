package com.example.graduationproject;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CourseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseFragment newInstance(String param1, String param2) {
        CourseFragment fragment = new CourseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ArrayAdapter yearAdapter;
    private Spinner yearSpinner;
    private ArrayAdapter termAdapter;
    private Spinner termSpinner;
    private ArrayAdapter areaAdapter;
    private Spinner areaSpinner;
    private ArrayAdapter majorAdapter;
    private Spinner majorSpinner;

    private String courseUniversity = "";


    private ListView courseListView;
    private CourseListAdapter adapter;
    private List<Course> courseList;

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        final RadioGroup courseUniversityGroup = (RadioGroup) getView().findViewById(R.id.courseUniversityGroup);
        yearSpinner = (Spinner) getView().findViewById(R.id.yearSpinner);
        termSpinner = (Spinner) getView().findViewById(R.id.termSpinner);
        areaSpinner = (Spinner) getView().findViewById(R.id.areaSpinner);
        majorSpinner = (Spinner) getView().findViewById(R.id.majorSpinner);

        courseUniversityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton courseButton = (RadioButton) getView().findViewById(i);
                courseUniversity = courseButton.getText().toString();

                yearAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.year,android.R.layout.simple_spinner_dropdown_item);
                yearSpinner.setAdapter(yearAdapter);

                termAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.term,android.R.layout.simple_spinner_dropdown_item);
                termSpinner.setAdapter(termAdapter);

                areaAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.area,android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(areaAdapter);

                if(courseUniversity.equals("Bachelor"))
                {
                    majorAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.universityMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);

                }//콤보박스에서 bachelor 선택시 학과보여지게

                else if(courseUniversity.equals("Master"))
                {
                    majorAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.graduateMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                }//콤보박스에서 master 선택시 학과보여지게

            }
        });

         areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){


            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        courseListView=(ListView)getView().findViewById(R.id.courseListView);
        courseList = new ArrayList<Course>();
        adapter = new CourseListAdapter(getContext().getApplicationContext(),courseList);
        courseListView.setAdapter(adapter);

        Button searchButton = (Button) getView().findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackgroundTask().execute();
            }
        });

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class BackgroundTask extends AsyncTask<Void,Void,String>
    {
        String target;

        @Override
        protected void onPreExecute(){    ///검색'search' 활성화 시

            try {
                target= "https://bakhoijae.cafe24.com/ES_CourseList.php?courseUniversity=" + URLEncoder.encode(courseUniversity,"UTF-8") +
                        "&courseYear=" + URLEncoder.encode(yearSpinner.getSelectedItem().toString().substring(0,4),"UTF-8") +   "&courseTerm=" + URLEncoder.encode(termSpinner.getSelectedItem().toString(),"UTF-8") +
                        "&courseArea=" + URLEncoder.encode(areaSpinner.getSelectedItem().toString(),"UTF-8") +   "&courseMajor=" + URLEncoder.encode(majorSpinner.getSelectedItem().toString(),"UTF-8");

            } catch (Exception e){
                e.printStackTrace();
            }


        }

        @Override
        protected String doInBackground(Void... voids) {

            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp=bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... voids){
            super.onProgressUpdate();
        }



  @Override
  public void onPostExecute(String result){
      try{
          courseList.clear();
          JSONObject jsonObject = new JSONObject(result);
          JSONArray jsonArray = jsonObject.getJSONArray("response");
          int count=0;
          int courseID; //강의 고유번호
          String courseUniversity; //학부 혹은 대학원
          int courseYear; // 해당 년도
          String courseTerm; //해당학기
          String courseArea; //강의 영역
          String courseMajor; //해당학과
          String courseGrade; //해당 학년
          String courseTitle; //강의 제목
          int courseCredit; //강의 학점
          int courseDivide; //강의 분반
          int coursePersonnel; //강의 제한 인원
          String courseProfessor; //강의 교수
          String courseTime; //강의 시간대
          String courseRoom; //강의실
          while(count<jsonArray.length())
          {
              JSONObject object = jsonArray.getJSONObject(count);
              courseID = object.getInt("courseID");
              courseUniversity = object.getString("courseUniversity");
              courseYear = object.getInt("courseYear");
              courseTerm = object.getString("courseTerm");
              courseArea = object.getString("courseArea");
              courseMajor = object.getString("courseMajor");
              courseGrade = object.getString("courseGrade");
              courseTitle = object.getString("courseTitle");
              courseCredit = object.getInt("courseCredit");
              courseDivide = object.getInt("courseDivide");
              coursePersonnel = object.getInt("coursePersonnel");
              courseProfessor = object.getString("courseProfessor");
              courseTime = object.getString("courseTime");
              courseRoom = object.getString("courseRoom");
              Course course= new Course(courseID, courseUniversity, courseYear, courseTerm, courseArea, courseMajor, courseGrade, courseTitle, courseCredit, courseDivide, coursePersonnel, courseProfessor, courseTime, courseRoom) ;
              courseList.add(course);
              count++;
          }
          if(count==0)
          {
              AlertDialog dialog;
              AlertDialog.Builder builder = new AlertDialog.Builder(CourseFragment.this.getActivity());
              dialog = builder.setMessage("There are no class")
                      .setNegativeButton("OK",null)
                      .create();
              dialog.show();
          }
          adapter.notifyDataSetChanged();
      } catch(Exception e){
          e.printStackTrace();
      }
  }

    }
}
