Flag = true;
Case1_benefit_ray = ceil(raylrnd(1, 1500, 10)*160);
while(Flag)
    SD = std(Case1_benefit_ray);
    M = mean(Case1_benefit_ray);
    cv = SD/M;
    if cv < 0.5 || cv > 0.6
        Case1_benefit_ray = ceil(raylrnd(1, 1500, 10)*160);
    else
        Flag = false; 
    end
end
