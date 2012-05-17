hold off
clf
A = load('dataforkoutput.csv');
for i=1:length(A)
	plot(A(i,1), A(i,2), (strcat('o',int2str(A(i,3)+1))))
	hold on
endfor