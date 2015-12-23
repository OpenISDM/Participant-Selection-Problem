clc
clear
T = 1000;       %time of experiments
R = 5;          %number of region%
P = 1500;       %number of participant%
B = 75;         %benefit variance
C = 20;         %cost variance
colValue = 'E';  % set column of value in excel 
col = 'F';      % set column of benefit and cost in excel
%a = char(double('A') + R);

%raw data
values_SD = zeros(T, 1);
values_M = zeros(T, 1);
values_CV = zeros(T, 1);
benefits_SD = zeros(T, 1);
benefits_M = zeros(T, 1);
benefits_CV = zeros(T, 1);
costs_SD = zeros(T, 1);
costs_M = zeros(T, 1);
costs_CV = zeros(T, 1);
CP = zeros(P, R);
CP_Avg = zeros(T, 1);
CP_Max = zeros(T, 1);
CP_Min = zeros(T, 1);
B_AboveAvg = zeros(T, 1);
CP_AboveAvg = zeros(T, 1);

%Generate TAS%
for i = 1:T
    value_uniform = unidrnd(14901, 1, R)+99;
    num = num2str(i);
    values_SD(i, 1) = std(value_uniform);
    values_M(i, 1) = mean(value_uniform);
    values_CV(i, 1) = values_SD(i, 1) / values_M(i, 1);
    fileName = ['data' num '.xls'];
    xlswrite(fileName, value_uniform, 'values', ['A2:' colValue '2']);
end

%Generate PSS%
for i = 1:T
    Flag = true;
    benefit_uniform = zeros(P, R);
    cost_uniform = zeros(P, R);
    while(Flag)
        seed = unidrnd(3, P, R);
        SD = std(seed);
        M = mean(seed);
        cv = SD/M;
        if cv > 0.5
            seed = unidrnd(3, P, R);
        else
            Flag = false;
        end
    end
    for j=1:P
        for k=1:R
            choose = seed(j,k);
            B_var = ceil(B/3);
            C_var = ceil(C/3);
            if (choose == 1)
                benefit_uniform(j,k) = unidrnd(B_var);
                cost_uniform(j,k) = unidrnd(C_var);
                if(benefit_uniform(j,k) < 5)
                    benefit_uniform(j,k) = 5;
                end
                if(cost_uniform(j,k) < 5)
                    cost_uniform(j,k) = 5;
                end
            elseif(choose == 2)
                benefit_uniform(j,k) = unidrnd(B_var)+B_var;
                cost_uniform(j,k) = unidrnd(C_var)+C_var;
            elseif(choose == 3)
                benefit_uniform(j,k) = unidrnd(B_var)+2*B_var;
                cost_uniform(j,k) = unidrnd(C_var)+2*C_var;
            end
            CP(j, k) = benefit_uniform(j,k) / cost_uniform(j,k);
        end
    end
    num = num2str(i);
    fileName = ['data' num '.xls'];
    xlswrite(fileName, benefit_uniform, 'benefits', ['B2:' col num2str(P+1)]);
    xlswrite(fileName, cost_uniform, 'costs', ['B2:' col num2str(P+1)]);
    
    %raw data estimate
    benefits_SD(i, 1) = std(benefit_uniform(:));
    benefits_M(i, 1) = mean(benefit_uniform(:));
    benefits_CV(i, 1) = benefits_SD(i, 1) / benefits_M(i, 1);
    costs_SD(i, 1) = std(cost_uniform(:));
    costs_M(i, 1) = mean(cost_uniform(:));
    costs_CV(i, 1) = costs_SD(i, 1) / costs_M(i, 1);
    CP_Avg(i, 1) = mean(CP(:));
    CP_Max(i, 1) = max(CP(:));
    CP_Min(i, 1) = min(CP(:));
    
    for a=1:length(benefit_uniform(:)); 
        B_AboveAvg(i)=length(find(benefit_uniform(:) > benefits_M(i, 1))); 
    end
    
    for a=1:length(CP(:)); 
        CP_AboveAvg(i)=length(find(CP(:) > CP_Avg(i, 1))); 
    end
end

%write raw data
fileName = 'rawdata.xls';
xlswrite(fileName, values_SD, 'Values', 'B2');
xlswrite(fileName, values_M, 'Values', 'C2');
xlswrite(fileName, values_CV, 'Values', 'D2');
xlswrite(fileName, benefits_SD, 'Benefits', 'B2');
xlswrite(fileName, benefits_M, 'Benefits', 'C2');
xlswrite(fileName, benefits_CV, 'Benefits', 'D2');
xlswrite(fileName, costs_SD, 'Costs', 'B2');
xlswrite(fileName, costs_M, 'Costs', 'C2');
xlswrite(fileName, costs_CV, 'Costs', 'D2');
xlswrite(fileName, CP_Avg, 'CP', 'B2');
xlswrite(fileName, CP_Max, 'CP', 'C2');
xlswrite(fileName, CP_Min, 'CP', 'D2');
xlswrite(fileName, B_AboveAvg, 'Benefits_Above');
xlswrite(fileName, CP_AboveAvg, 'CP_Above');