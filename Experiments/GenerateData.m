T = 100;       %time of experiments
R = 5;          %number of region%
P = 1500;       %number of participant%
B = 75;         %benefit variance
C = 20;         %cost variance
colValue = 'E';  % set column of value in excel 
col = 'F';      % set column of benefit and cost in excel
%a = char(double('A') + R);

%Generate TAS%
for i = 1:T
    Flag = true;
    value_uniform = unidrnd(14901, 1, R)+99;
    while(Flag)
        SD = std(value_uniform);
        M = mean(value_uniform);
        cv = SD/M;
        if cv > 0.6
            value_uniform = unidrnd(14901, 1, R)+100;
        else
            Flag = false;
            num = num2str(i);
            fileName = ['data' num '.xls'];
            xlswrite(fileName, value_uniform, 'values', ['A2:' colValue '2']);
        end
    end
end

%Generate PSS%
for i = 1:T
    Flag = true;
    benefit_uniform = zeros(1500, 5);
    cost_uniform = zeros(1500, 5);
    for j=1:P
        for k=1:R
            choose = unidrnd(3);
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
        end
    end
    while(Flag)
        SD = std(benefit_uniform);
        M = mean(benefit_uniform);
        %cv = SD/M;
        %if cv > 0.6
            %benefit_uniform = unidrnd(100, P, R);
        %else
            Flag = false;
            num = num2str(i);
            fileName = ['data' num '.xls'];
            xlswrite(fileName, benefit_uniform, 'benefits', ['B2:' col num2str(P+1)]);
            xlswrite(fileName, cost_uniform, 'costs', ['B2:' col num2str(P+1)]);
        %end
    end
end
