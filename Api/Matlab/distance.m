%%����·�����벢��ֵ
function [D] = distance(n,Coordinate,m)
    for k = 1:m
        for i=1:n
            for j=1:n
                if i~=j
                    D(i,j,k)=((Coordinate(i,1,k)-Coordinate(j,1,k))^2+(Coordinate(i,2,k)-Coordinate(j,2,k))^2)^0.5;
                else
                    D(i,j,k)=eps;      %i=jʱ�����㣬���ں������������Ҫȡ��������eps��������Ծ��ȣ���ʾ
                end
                    D(j,i,k)=D(i,j,k);   %�Գƾ���
            end
        end
    end
end