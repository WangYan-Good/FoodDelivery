%%计算路径距离并赋值
function [D] = distance(n,Coordinate,m)
    for k = 1:m
        for i=1:n
            for j=1:n
                if i~=j
                    D(i,j,k)=((Coordinate(i,1,k)-Coordinate(j,1,k))^2+(Coordinate(i,2,k)-Coordinate(j,2,k))^2)^0.5;
                else
                    D(i,j,k)=eps;      %i=j时不计算，由于后面的启发因子要取倒数，用eps（浮点相对精度）表示
                end
                    D(j,i,k)=D(i,j,k);   %对称矩阵
            end
        end
    end
end