/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(function () {
            d3.json("/Tesi-SignedGraphDrawing/Controller", function (data) {
                //Transform the XML data into a proper format used by the algorithm
                var raw_edges = data.interEdges;
                var raw_nodes = data.nodes;
                var raw_iedges = data.intraEdges;
                var eedges = [];
                var nnodes = {};
                var iedges = [];
                var min_x = Number.MAX_VALUE;
                var max_x = 0;
                var min_y = Number.MAX_VALUE;
                var max_y = 0;


                for (var i = 0; i < raw_nodes.length; i++) {
                    var key = raw_nodes[i].id;

                    //var x = Math.abs(parseFloat(raw_nodes[i].x));
                    var x = parseFloat(raw_nodes[i].x);
                    //var name = raw_nodes[i].id;
                    //var y = Math.abs(parseFloat(raw_nodes[i].y));
                    var y = parseFloat(raw_nodes[i].y);
                    
                    nnodes[key] = {
                        'x': x,
                        'y': y
                    };
                    min_x = Math.min(min_x, x);
                    max_x = Math.max(max_x, x);
                    min_y = Math.min(min_y, y);
                    max_y = Math.max(max_y, y);
                }

                var cpos_inter = 0;
                for (var i = 0; i < raw_edges.length; i++) {
                          
                    if(raw_edges[i].sign === "+"){
                        cpos_inter = cpos_inter+1;
                    }
                    
                    eedges.push({
                        'source': raw_edges[i].source,
                        'target': raw_edges[i].target,
                        'sign': raw_edges[i].sign
                    });
                
                }


                 
                    
                for(var i = 0; i < raw_iedges.length; i++){
                    
                    iedges.push({
                        'source': raw_iedges[i].source,
                        'target': raw_iedges[i].target,
                        'consecutive': raw_iedges[i].consecutive,
                        'sign': raw_iedges[i].sign
                    });
                }
                
                
                console.log('Nodes', nnodes);
                console.log('Edges', eedges);
                console.log('iEdges', iedges);


                var width = 1200;
                var height = 650;
                var mid_width = width/2;
                var mid_height = height/2;
                
                
                
                //old values 1000,50
                var range_y_min = 560;
                var range_y_max = 50;
                
                if(cpos_inter != 0){
                    range_y_min = range_y_min/2 + 80;
                }
               
                
                var new_scale_x = d3.scaleLinear().domain([min_x, max_x]).range([900, 150]);
                var new_scale_y = d3.scaleLinear().domain([min_y, max_y]).range([range_y_min, range_y_max]);
                for (var i = 0; i < raw_nodes.length; i++) {
                    nnodes[i].x = new_scale_x(nnodes[i].x);
                    nnodes[i].y = new_scale_y(nnodes[i].y);
                }

                
               
                var svg = d3.select("#svg").append("svg")
                    .attr("width", width)
                    .attr("height", height);

               
                svg = svg.append('g');              
                //svg.attr('transform', 'translate(20, 70)');



                var xscale = d3.scaleLinear()
                    .domain([-1, 1])
                    .range([new_scale_x(-1), new_scale_x(1)]);

                var yscale = d3.scaleLinear()
                    .domain([-max_y, max_y])
                    .range([height-10, 10]);

                var x_axis = d3.axisBottom()
                        .scale(xscale);

                var y_axis = d3.axisLeft()
                        .scale(yscale);

                svg.append("g")
                   .attr("transform", "translate(" +  new_scale_x(0) + ", 10)")
                   .call(y_axis)
                   .selectAll(".tick").remove();

                var xAxisTranslate = height/2 + 10;

                svg.append("g")
                        .attr("transform", "translate(0, " + new_scale_y(0)  +")")
                        .call(x_axis)
                        .selectAll(".tick").remove();

                //INTER EDGES
                svg
                    .selectAll('mylinks')
                    .data(eedges)
                    .enter()
                    .append('path')
                    .attr('d', function (d) {
                      start = nnodes[d.source].x;   // X position of start node on the X axis
                      end = nnodes[d.target].x;      // X position of end node
                      start_y = nnodes[d.source].y;
                      end_y = nnodes[d.target].y;
                      
                      orientation = 1;
                      if((d.sign === "+" && start<=end) || (d.sign === "-" && start>end)){
                        orientation = 0;
                      }
                      
                      return ['M', start, start_y,    // the arc starts at the coordinate x=start, y=height-30 (where the starting node is)
                        'A',                            // This means we're gonna build an elliptical arc
                        (start - end), ',',    // Next 2 lines are the coordinates of the inflexion point. Height of this point is proportional with start - end distance
                        (start - end), 0, 0, ',',
                         orientation, end, ',', end_y] // We always want the arc on top. So if end is before start, putting 0 here turn the arc upside down.
                        .join(' ');
                    })
                    .style("fill", "none")
                    .attr("stroke", function(d){
                        if(d.sign==="+"){
                            return "blue";
                        }else{
                            return "red";
                        }      
                });
                
                //INTRA EDGES
                svg
                .selectAll('mylinks')
                .data(iedges)
                .enter()
                .append('path')
                .attr('d', function (d) {
                  start = nnodes[d.source].y;    // X position of start node on the X axis
                  end = nnodes[d.target].y;  
                  x = nnodes[d.source].x;// X position of end node
                  sign = d.sign;
                      
                  
                  var orientation = 1;
                  if(sign === "+"){
                    orientation = 0;
                  }
                  

                  return ['M', x, start,    // the arc starts at the coordinate x=start, y=height-30 (where the starting node is)
                    'A',                            // This means we're gonna build an elliptical arc
                    (start - end)/4, ',',    // Next 2 lines are the coordinates of the inflexion point. Height of this point is proportional with start - end distance
                    (start-end)/2, 0, 0, ',',
                    orientation, x, ',', end] // We always want the arc on top. So if end is before start, putting 0 here turn the arc upside down.
                    .join(' ');
                 
                })
                .style("fill", "none")
                .attr("stroke", function(d){
                    if(d.sign==="+"){
                        return "blue";
                    }else{
                        return "red";
                    }
                    
                });
                
                
                //draw nodes
                svg
                .selectAll('.node')
                .data(d3.entries(nnodes))
                .enter().append('circle')
                .attr('cx', function (d) { return d.value.x; })
                .attr('cy', function (d) { return d.value.y; })
                .attr('r', '6')
                .attr('fill', 'black');
   
            });
            
            
            
          
        })