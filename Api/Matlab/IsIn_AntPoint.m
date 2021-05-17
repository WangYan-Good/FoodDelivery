function [bool] = IsIn_AntPoint(Arr_1,Arr_2,i,m)
    n = size(Arr_2,1);
    for j =1:m
        if i==j
            continue;
        end
        for k = 1:n
            if Arr_1(1,:) == Arr_2(k,:,j)
                bool = 1;
                return;
            end
        end

    end
    bool = 0;
end