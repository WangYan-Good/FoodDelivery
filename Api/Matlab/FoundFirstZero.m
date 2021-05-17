function [i] = FoundFirstZero(Arr)
n = size(Arr,1);
for i = 1:n
    if Arr(i,:) == 0
        return;
    else
        continue;
    end
end
end