import * as React from "react";
import * as moment from "moment";
import { CalendarEvent, RecurringPeriod } from "../calendar/calendar-models";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

export interface IEditEventProps
{
	event: CalendarEvent | null;
	onChange: (event: CalendarEvent) => void;
}

export interface IEditEventState
{

}

export class EditEvent extends React.Component<IEditEventProps, IEditEventState> {

	constructor(props: IEditEventProps)
	{
		super(props);

		this.state = {
		};
	}

	private updateValue(value: any, prop: string)
	{
		if (!!this.props.event)
		{
			let newEvent = this.props.event.clone();
			(newEvent as any)[prop] = value;

			this.props.onChange(newEvent);
		}
	}

	private onTextChange(e: React.ChangeEvent<HTMLInputElement>, prop: string)
	{
		this.updateValue(e.target.value, prop);
	}

	private onCheckboxChange(e: React.ChangeEvent<HTMLInputElement>, prop: string)
	{
		this.updateValue(e.target.checked, prop);
	}

	private onSelectChange(e: React.ChangeEvent<HTMLSelectElement>, prop: string)
	{
		this.updateValue(parseInt(e.target.value), prop);
	}

	private onDateChange(date: moment.Moment | null, prop: string)
	{
		if (!date || !this.props.event)
			return;

		this.updateValue(date, prop);
	}

	private renderDatePickerWithRestriction(start: moment.Moment, end: moment.Moment, isStart: boolean, isEnd: boolean, dateFormat: string, timeFormat: string, timeIntervals: number)
	{
		return (<DatePicker className="form-control"
		            selected={isStart ? start : end}
					selectsStart={isStart}
		            selectsEnd={isEnd}
		            startDate={start}
		            endDate={end}
		            dateFormat={dateFormat}
		            showTimeSelect
		            timeIntervals={timeIntervals}
		            timeFormat={timeFormat}
		            onChange={(date) => this.onDateChange(date, isStart ? "start" : "end")} />);
	}

	private renderDatePicker(start: moment.Moment, end: moment.Moment, isStart: boolean, isEnd: boolean, dateFormat: string, timeFormat: string, timeIntervals: number)
	{
		return (<DatePicker className="form-control"
							selected={isStart ? start : end}
							selectsStart={isStart}
		                    selectsEnd={isEnd}
		                    dateFormat={dateFormat}
		                    showTimeSelect
		                    timeIntervals={timeIntervals}
		                    timeFormat={timeFormat}
		                    onChange={(date) => this.onDateChange(date, isStart ? "start" : "end")} />);
	}

	public render()
	{
		if (!this.props.event)
			return <div className="edit-event-form"><i>Select event to edit</i></div>;

		let dateFormat = "DD.MM.YYYY HH:mm";
		let timeFormat = "HH:mm";
		let timeIntervals = 30;

		let datePickerStart = (!!this.props.event.start && !!this.props.event.end)
			? this.renderDatePickerWithRestriction(this.props.event.start, this.props.event.end, true, false, dateFormat, timeFormat, timeIntervals)
			: this.renderDatePicker(this.props.event.start as moment.Moment, this.props.event.end as moment.Moment, true, false, dateFormat, timeFormat, timeIntervals);

		let datePickerEnd = (!!this.props.event.start && !!this.props.event.end)
			? this.renderDatePickerWithRestriction(this.props.event.start, this.props.event.end, false, true, dateFormat, timeFormat, timeIntervals)
			: this.renderDatePicker(this.props.event.start as moment.Moment, this.props.event.end as moment.Moment, false, true, dateFormat, timeFormat, timeIntervals);

		return <div className="edit-event-form">
			<div className="col-xs-6 col-sm-4 col-md-4 col-lg-3">

				<div className="form-group">
					<label className="control-label">Title</label>
					<input type="text" className="form-control" value={this.props.event.title} onChange={(e) => this.onTextChange(e, "title")} />
				</div>

				<div className="form-group">
					<label className="control-label">Start</label>
					{datePickerStart}
				</div>

			</div>

			<div className="col-xs-6 col-sm-4 col-md-4 col-lg-3">

				<div className="form-group">
					<label className="control-label">Recurring</label>
					<select className="form-control" value={this.props.event.recurring} onChange={(e) => this.onSelectChange(e, "recurring")}>
						<option value={RecurringPeriod.None}>{RecurringPeriod[RecurringPeriod.None]}</option>
						<option value={RecurringPeriod.Day}>{RecurringPeriod[RecurringPeriod.Day]}</option>
						<option value={RecurringPeriod.Week}>{RecurringPeriod[RecurringPeriod.Week]}</option>
						<option value={RecurringPeriod.Month}>{RecurringPeriod[RecurringPeriod.Month]}</option>
					</select>
				</div>
				
				<div className="form-group">
					<label className="control-label">End</label>
					{datePickerEnd}
				</div>

			</div>

			<div className="col-xs-6 col-sm-4 col-md-4 col-lg-3">

				<div className="form-group">
					<label className="control-label">
						<input type="checkbox" className="checkbox" checked={this.props.event.allDay} onChange={(e) => this.onCheckboxChange(e, "allDay")} />
						All day
					</label>
				</div>

			</div>

		</div>;
	}
}
