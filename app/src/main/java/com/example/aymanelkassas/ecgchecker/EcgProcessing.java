package com.example.aymanelkassas.ecgchecker;

import java.util.ArrayList;

import static java.util.Collections.sort;

/**
 * Created by Ayman Elkassas on 5/17/2018.
 */

public class EcgProcessing {

    ArrayList<Integer> signal,sample,indices_of_Max,differ;
    int max=0;
    int start=24;

    String result="";
    static boolean regularORIrr=true;

    //TODO:Large square
    double Lduration=.2;
    //TODO:Small square
    double Sduration=.04;

    public EcgProcessing(ArrayList<Integer> signal) {
        this.signal = signal;
        sample=new ArrayList<>();
        indices_of_Max=new ArrayList<>();
        differ=new ArrayList<>();
    }

    public String regularOrIrregular()
    {
        indices_of_Max=R_Waves(signal,24,signal.size());

        for(int i=0;i<indices_of_Max.size();i++)
        {
            if(i!=indices_of_Max.size()-1)
            {
                differ.add(indices_of_Max.get(i+1)-indices_of_Max.get(i));
            }
            else
            {
                for(int j=0;j<differ.size();j++)
                {
                    if(j!=differ.size()-1 && j<5)
                    {
                        if(differ.get(j)!=differ.get(j+1))
                        {
                            regularORIrr=false;
                            result= "Irregular ECG Signal";
                            break;
                        }
                    }
                    else
                    {
                        result= "Regular ECG Signal";
                        break;
                    }

                }
            }
        }
        return result;
    }

    public double Duration_Of_Cardio_Cycle()
    {
        if(regularORIrr)
        {
            return differ.get(0)*Lduration;
        }
        else
        {
            int sum=0;
            for(int i=0;i<differ.size();i++)
            {
                sum+=differ.get(i);
            }
            double avg=sum/(differ.size());
            return avg*Lduration;
        }
    }

    public double HR()
    {
        if(regularORIrr)
        {
            return (300/differ.get(0));
        }
        else
        {
            int sum=0;
            for(int i=0;i<differ.size();i++)
            {
                sum+=differ.get(i);
            }
            double avg=sum/(differ.size());
            return (300/avg);
        }
    }

    public ArrayList<String> PR_Normality()
    {
        ArrayList<String> result=new ArrayList<>();
        ArrayList<Integer> P_Points=new ArrayList<>();

        //TODO:PR INTERVAL
        for(int i=42;i<57;i++)
        {
            P_Points.add(signal.get(i));
        }

        indices_of_Max=R_Waves(P_Points,0,P_Points.size());

        for(int i=0;i<indices_of_Max.size();i++)
        {
            P_Points.set(indices_of_Max.get(i),0);
        }

        int mx=0;
        for(int j=0;j<6;j++)
        {
            if(mx<P_Points.get(j))
            {
                mx=P_Points.get(j);
            }
        }

        int dif=(indices_of_Max.get(0)-1)-(P_Points.indexOf(mx)+1);

        if(dif<0)
        {
            dif=dif*-1;
        }

        if(dif>2 && dif<6)
        {
            result.add("No 1st DHB OR Pre-Excitation Syndrome");
        }
        else if(dif>6)
        {
            result.add("1st degree Heart Block");
        }
        else if(dif<=2)
        {
            result.add("Pre-Excitation Syndrome");
        }

        //TODO:Amplitude of p wave

        int top_p_wave=mx;
        int lower_p_wave=signal.get(signal.indexOf(mx)+1);

        double p_amplitude=top_p_wave-lower_p_wave;

        if(p_amplitude<0)
        {
            p_amplitude=p_amplitude*-1;
        }

        if(p_amplitude<60 && p_amplitude>40)
        {
            result.add("No LT.Rt Atrial Enlargement");
        }
        else if(p_amplitude>60)
        {
            result.add("Left Atrial Enlargement");
        }
        else if(p_amplitude<60)
        {
            result.add("Right Atrial Enlargement");
        }

        return result;
    }

    ArrayList<Integer> R_Waves(ArrayList<Integer> signalStrip,int start,int end)
    {
        indices_of_Max=new ArrayList<>();
        sample=new ArrayList<>();
        for(int i=start;i<end;i++) {

            if(i<(start+6))
            {
                sample.add(signalStrip.get(i));
            }
            else
            {
                start=i-1;
                max=sample.get(0);
                for(int j=0;j<sample.size();j++)
                {
                    if(max<sample.get(j))
                    {
                        max=sample.get(j);
                    }
                }
                indices_of_Max.add(i-(6-sample.indexOf(max)));
            }

        }

        return indices_of_Max;
    }

    ArrayList<String> LVH()
    {
        ArrayList<String> result=new ArrayList<>();
        indices_of_Max=R_Waves(signal,24,signal.size());

        int R_value=signal.get(indices_of_Max.get(0));
        if(R_value>850)
        {
            result.add("LT Ventricular Hypertrophy");
        }
        else
        {
            result.add("NO LVH");
            result.add("NO RVH");
        }

        return result;
    }

    String BundleBranchBlock()
    {
        String result="";

        indices_of_Max=R_Waves(signal,24,signal.size());
        int R_v=signal.get(indices_of_Max.get(0));

        if(R_v+10 <signal.get(indices_of_Max.get(0)+2) && R_v-10 >signal.get(indices_of_Max.get(0)+2))
        {
            result="RT.LT Bundle Branch Block";
        }
        else
        {
            result="No RT.LT Bundle branch block";
        }

        return result;
    }

    String TachyORBrady_Arithmia()
    {
        String result="";
        double hr=HR();

        if(regularORIrr && (hr>95 || hr<55))
        {
            if(hr>95)
            {
                result="Tachy Arithmia, may be (Sines Tachy Cardia, Paroxysmal Attack, Paroxysmal nodal TC, Paroxysmal ventricular TC, Atrial Flutter, Atrial Fibrillation)";
            }
            else if(hr<55)
            {
                result="Brady Arithmia";
            }
        }
        else if(!regularORIrr &&(hr<95 || hr>55))
        {
            result="Arithmia";
        }
        else
        {
            result="No Arithmia";
        }

        return result;
    }

    String StrainPatternOfVH()
    {
        String result="";
        ArrayList<Integer> sample=new ArrayList<>();
        indices_of_Max=R_Waves(signal,24,signal.size());

        for(int i =1;i<5;i++)
        {
            sample.add(signal.get(indices_of_Max.get(0)+i));
        }

        for (int j=0;j<sample.size();j++)
        {
            if(sample.get(j)<(report.average-100))
            {
                result="LT Strain Pattern Of ventricular Hypertrophy";
                return result;
            }
        }
        result="No LT SVH";
        return result;
    }
}
