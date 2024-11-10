import React from "react";

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
import { Label, Pie, PieChart } from "recharts";

const chartConfig = {
  visitors: {
    label: "Visitors",
  },
  chrome: {
    label: "Chrome",
    color: "hsl(var(--chart-1))",
  },
  safari: {
    label: "Safari",
    color: "hsl(var(--chart-2))",
  },
  firefox: {
    label: "Firefox",
    color: "hsl(var(--chart-3))",
  },
  edge: {
    label: "Edge",
    color: "hsl(var(--chart-4))",
  },
  other: {
    label: "Other",
    color: "hsl(var(--chart-5))",
  },
} satisfies ChartConfig;

interface MyComponentProps {
  data: MatchData[];
  title: string;
  description?: string;
}

const PieChartGraph: React.FC<MyComponentProps> = ({
  data,
  title,
  description,
}) => {
  const totalGames = data.length;

  function transformMatchData(matches: MatchData[]): PieChartData[] {
    let winCount = 0;
    let loseCount = 0;
    let drawCount = 0;
    let onGoingCount = 0;

    // Fix this logic tmr
    // TODO
    matches.forEach((match) => {
      if (match.player1Score === null || match.player2Score === null) {
        onGoingCount++;
      } else if (match.player1Score > match.player2Score) {
        winCount++;
      } else if (match.player1Score < match.player2Score) {
        loseCount++;
      } else {
        drawCount++;
      }
    });

    return [
      { state: "Win", count: winCount, fill: "var(--color-chrome)" },
      { state: "Lost", count: loseCount, fill: "var(--color-safari)" },
      { state: "Draw", count: drawCount, fill: "var(--color-firefox)" },
      { state: "Ongoing", count: onGoingCount, fill: "var(--color-edge)" },
    ];
  }

  const parseDate: PieChartData[] = transformMatchData(data);

  // Calculate percentages for the footer
  const winPercentage =
    ((parseDate.find((d) => d.state === "Win")?.count || 0) / totalGames) * 100;
  const losePercentage =
    ((parseDate.find((d) => d.state === "Lost")?.count || 0) / totalGames) *
    100;
  const drawPercentage =
    ((parseDate.find((d) => d.state === "Draw")?.count || 0) / totalGames) *
    100;
  const ongoingPercentage =
    ((parseDate.find((d) => d.state === "Ongoing")?.count || 0) / totalGames) *
    100;

  return (
    <Card className="flex flex-col">
      <CardHeader className="items-center pb-0">
        <CardTitle>{title}</CardTitle>
        <CardDescription className="text-center">{description}</CardDescription>
      </CardHeader>
      <CardContent className="flex-1 pb-0">
        <ChartContainer
          config={chartConfig}
          className="mx-auto aspect-square max-h-[250px]"
        >
          <PieChart>
            <ChartTooltip
              cursor={false}
              content={<ChartTooltipContent hideLabel />}
            />
            <Pie
              data={parseDate}
              dataKey="count"
              nameKey="state"
              innerRadius={60}
              strokeWidth={5}
            >
              <Label
                content={({ viewBox }) => {
                  if (viewBox && "cx" in viewBox && "cy" in viewBox) {
                    return (
                      <text
                        x={viewBox.cx}
                        y={viewBox.cy}
                        textAnchor="middle"
                        dominantBaseline="middle"
                      >
                        <tspan
                          x={viewBox.cx}
                          y={viewBox.cy}
                          className="fill-foreground text-3xl font-bold"
                        >
                          {totalGames.toLocaleString()}
                        </tspan>
                        <tspan
                          x={viewBox.cx}
                          y={(viewBox.cy || 0) + 24}
                          className="fill-muted-foreground"
                        >
                          Total Games
                        </tspan>
                      </text>
                    );
                  }
                }}
              />
            </Pie>
          </PieChart>
        </ChartContainer>
      </CardContent>
      <CardFooter className="flex-col gap-2 text-sm">
        <div className="flex items-center gap-2 font-medium leading-none">
          Wins: {winPercentage.toFixed(1)}% | Losses:{" "}
          {losePercentage.toFixed(1)}% | Draws: {drawPercentage.toFixed(1)}% |
          Ongoing: {ongoingPercentage.toFixed(1)}%
        </div>
        <div className="leading-none text-muted-foreground">
          Statistics based on total games played
        </div>
      </CardFooter>
    </Card>
  );
};

export default PieChartGraph;

export interface MatchData {
  id: number;
  durationInMinutes: number | null;
  status: string;
  bracket: string;
  player1Score: number | null;
  player2Score: number | null;
  matchDate: string;
  round: number | null;
  player1: {
    id: number;
    username: string;
    userType: string;
    name: string;
    eloRating: number;
  };
  player2: {
    id: number;
    username: string;
    userType: string;
    name: string;
    eloRating: number;
  };
  tournament: {
    id: number;
    name: string;
    startDate: string;
    endDate: string | null;
    location: string;
    status: string;
    minEloRating: number;
    maxEloRating: number;
    format: string;
  };
  winner: number | null;
  punchesPlayer1: number;
  punchesPlayer2: number;
  dodgesPlayer1: number;
  dodgesPlayer2: number;
  koByPlayer1: boolean;
  koByPlayer2: boolean;
}

export interface PieChartData {
  state: string;
  count: number;
  fill: string;
}
