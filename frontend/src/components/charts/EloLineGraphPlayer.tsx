"use-client";

import React from "react";

import { TrendingUp } from "lucide-react";
import { Area, AreaChart, CartesianGrid, XAxis, YAxis } from "recharts";

import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart";

const chartConfig = {
  desktop: {
    label: "Desktop",
    color: "hsl(var(--chart-1))",
  },
  mobile: {
    label: "Mobile",
    color: "hsl(var(--chart-2))",
  },
} satisfies ChartConfig;

interface MyComponentProps {
  data: EloRecord[];
  title: string;
  description: string;
}

const EloLineGraph: React.FC<MyComponentProps> = ({
  data,
  title,
  description,
}) => {
  function transformEloData(data: EloRecord[]): ChartData[] {
    const chartData: ChartData[] = [
      { match: "Match 0", elo: 1000 }, // Default starting point
    ];

    const transformedData = data.map((entry, index) => ({
      match: "Match " + (index + 1).toString(),
      elo: Math.round(entry.newRating), // Round the elo for simplicity
    }));

    return chartData.concat(transformedData);
  }

  const parsedData: ChartData[] = transformEloData(data);

  return (
    <Card className="w-[500px]">
      <CardHeader className="text-center">
        <CardTitle>{title}</CardTitle>
        <CardDescription>{description}</CardDescription>
      </CardHeader>
      <CardContent>
        <ChartContainer config={chartConfig}>
          <AreaChart
            accessibilityLayer
            data={parsedData}
            margin={{
              left: 12,
              right: 12,
            }}
          >
            <CartesianGrid vertical={false} />
            <YAxis
              domain={[800, 2000]} // Lower bound at 2000, upper bound adjusts with data
              tickMargin={8}
              allowDataOverflow={false} // Keeps the auto scaling on the upper bound
            />
            <ChartTooltip cursor={false} content={<ChartTooltipContent />} />
            <defs>
              <linearGradient id="fillDesktop" x1="0" y1="0" x2="0" y2="1">
                <stop
                  offset="5%"
                  stopColor="var(--color-desktop)"
                  stopOpacity={0.8}
                />
                <stop
                  offset="95%"
                  stopColor="var(--color-desktop)"
                  stopOpacity={0.1}
                />
              </linearGradient>
            </defs>
            <Area
              dataKey="elo"
              type="natural"
              fill="url(#fillDesktop)"
              fillOpacity={0.4}
              stroke="var(--color-desktop)"
              stackId="a"
            />
          </AreaChart>
        </ChartContainer>
      </CardContent>
    </Card>
  );
};

interface EloRecord {
  id: number;
  date: string;
  oldRating: number;
  newRating: number;
  changeReason: string;
  match: {
    id: number;
  };
}

interface ChartData {
  match: string;
  elo: number;
}

export default EloLineGraph;
