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

                for (var i = 0; i < raw_edges.length; i++) {
                          
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

                //old values 1000,50
                var new_scale_x = d3.scaleLinear().domain([min_x, max_x]).range([900, 150]);
                var new_scale_y = d3.scaleLinear().domain([min_y, max_y]).range([560, 50]);
                for (var i = 0; i < raw_nodes.length; i++) {
                    nnodes[i].x = new_scale_x(nnodes[i].x);
                    nnodes[i].y = new_scale_y(nnodes[i].y);
                }

                //Run the FDEB algorithm using default values on the data
                var fbundling = d3.ForceEdgeBundling().nodes(nnodes).edges(eedges);
                var results = fbundling();

                var svg = d3.select("#svg").append("svg")
                    .attr("width", 1200)
                    .attr("height", 600);

                var mid_width = 1200/2;
                
                svg = svg.append('g');
                svg.append('rect').attr({
                    'width': 1200,
                    'height': 600,
                    'fill': 'white'
                });
                svg.attr('transform', 'translate(20, 20)');


                var d3line = d3.line()
                    .x(function (d) {
                        return d.x;
                    })
                    .y(function (d) {
                        return d.y;
                    })
                    .curve(d3.curveBasis);
                //plot the data
                for (var i = 0; i < results.length; i++) {
                    svg.append("path").attr("d", d3line(results[i]))
                        .style("stroke-width", 1)
                        .style("stroke", "#ff2222")
                        .style("fill", "none")
                        .style('stroke-opacity', 1);
                }

                
        
        
        
               
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
                  orientation = 1;
                  if(d.sign === "+"){
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