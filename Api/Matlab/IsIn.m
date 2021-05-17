function [Bool] = IsIn(Arr_1,Arr_2)
    n = size(Arr_2,1);
    for i = 1:n
        if Arr_1(1,:)==Arr_2(i,:)
            Bool = i;
            return;
        else
            Bool = 0;
        end
    end
end